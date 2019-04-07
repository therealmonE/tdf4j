package io.github.therealmone.tdf4j.generator.impl;

import io.github.therealmone.tdf4j.commons.Dependency;
import io.github.therealmone.tdf4j.commons.Module;
import io.github.therealmone.tdf4j.commons.model.ebnf.First;
import io.github.therealmone.tdf4j.commons.model.ebnf.Follow;
import io.github.therealmone.tdf4j.commons.model.ebnf.Production;
import io.github.therealmone.tdf4j.generator.Generator;
import io.github.therealmone.tdf4j.generator.Template;
import io.github.therealmone.tdf4j.generator.templates.ImmutableMethodTemplate;
import io.github.therealmone.tdf4j.generator.templates.MethodTemplate;
import io.github.therealmone.tdf4j.generator.templates.ParserTemplate;
import io.github.therealmone.tdf4j.generator.templates.logic.CodeBlock;
import io.github.therealmone.tdf4j.parser.Parser;
import io.github.therealmone.tdf4j.parser.config.AbstractParserModule;
import org.joor.Reflect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ParserGenerator implements Generator<Parser> {

    @Override
    public Parser generate(Module module) {
        if(!(module instanceof AbstractParserModule)) {
            throw new RuntimeException("Parser can be generated only from AbstractParserModule");
        }
        return process(((AbstractParserModule) module).build());
    }

    private Parser process(final AbstractParserModule module) {
        final String generatedClassName = "AutoGeneratedParserFrom_" + module.getClass().getName().replaceFirst(module.getClass().getPackage().getName() + ".", "");
        final ParserTemplate parser = build(module, generatedClassName, module.getClass().getPackage().getName());
        System.out.println(parser.build());
        return Reflect.compile(module.getClass().getPackage().getName() + "." + generatedClassName,
                parser.build()
        ).create(args(
                module.getGrammar().firstSet(),
                module.getGrammar().followSet(),
                module.getEnvironment().dependencies()
        )).get();
    }

    private ParserTemplate build(final AbstractParserModule module, final String className, final String pack) {
        final ParserTemplate.Builder parserBuilder = new ParserTemplate.Builder()
                .className(className)
                .pack(pack)
                .environment(module.getEnvironment())
                .imports(Template.IMPORTS.template().render());
        if(module.getGrammar().initProduction() == null) {
            throw new RuntimeException("Initial production is null");
        }
        //noinspection ConstantConditions
        parserBuilder.initProd(module.getGrammar().initProduction());
        parserBuilder.addAllMethods(collectMethods(module.getGrammar().productions()));
        return parserBuilder.build();
    }

    private List<MethodTemplate> collectMethods(final List<Production> productions) {
        final Map<String, MethodTemplate.Builder> declaredMethods = new HashMap<>();
        for (final Production production : productions) {
            if(!declaredMethods.containsKey(production.identifier())) {
                declaredMethods.put(production.identifier(), new MethodTemplate.Builder()
                        .name(production.identifier())
                        .comment(production.toString().replaceAll("\n", "\n//"))
                );
            }

            final MethodTemplate.Builder builder = declaredMethods.get(production.identifier());
            production.elements().forEach(element -> {
                final CodeBlock codeBlock = CodeBlock.fromElement(element);
                if(codeBlock != null) {
                    builder.addCodeBlocks(codeBlock);
                }
            });
            builder.inlineAction(production.inlineAction().code());
        }
        return declaredMethods.values().stream().map((Function<MethodTemplate.Builder, MethodTemplate>) ImmutableMethodTemplate.Builder::build).collect(Collectors.toList());
    }

    private Object[] args(final First first, final Follow follow, final Dependency[] dependencies) {
        final Object[] args = new Object[dependencies.length + 2];
        args[0] = first;
        args[1] = follow;
        for (int i = 0; i < dependencies.length; i++) {
            args[i + 2] = dependencies[i].instance();
        }
        return args;
    }
}
