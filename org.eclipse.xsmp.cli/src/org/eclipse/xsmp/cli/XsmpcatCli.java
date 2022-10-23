/*******************************************************************************
* Copyright (C) 2020-2022 THALES ALENIA SPACE FRANCE.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.xsmp.cli;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsmp.XsmpcatStandaloneSetup;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class XsmpcatCli
{
  protected static final Logger LOG = Logger.getLogger(XsmpcatCli.class);

  public static final String VALIDATE_OPTION = "validate";

  public static final String GENERATE_OPTION = "generate";

  public static final String SMDL_DIR_OPTION = "smdl_dir";

  public static final String CONTEXT_OPTION = "context";

  public static final String HELP_OPTION = "help";

  static
  {
    @SuppressWarnings("unused")
    final Class< ? >[] classes = {
      org.apache.log4j.ConsoleAppender.class,
      org.apache.log4j.DailyRollingFileAppender.class,
      org.apache.log4j.PatternLayout.class };
  }

  public static void main(String[] args) throws ParseException
  {
    final var injector = new XsmpcatStandaloneSetup().createInjectorAndDoEMFRegistration();
    final var main = injector.getInstance(XsmpcatCli.class);
    main.execute(args);
  }

  @Inject
  private Provider<ResourceSet> resourceSetProvider;

  @Inject
  private IResourceValidator validator;

  @Inject
  private GeneratorDelegate generator;

  @Inject
  private JavaIoFileSystemAccess fileAccess;

  @Inject
  private IResourceServiceProvider resourceServiceProvider;

  @Inject
  private IOutputConfigurationProvider outputConfigurationProvider;

  @Inject
  private IResourceFactory resourceFactory;

  protected Options getOptions()
  {
    final var options = new Options();
    options.addOption("s", SMDL_DIR_OPTION, true,
            "The relative directory containing the xsmpcat files to validate/generate (default ./smdl)");

    options.addOption("v", VALIDATE_OPTION, false, "Validate files");
    options.addOption("g", GENERATE_OPTION, false, "Run generation");
    options.addOption("h", HELP_OPTION, false, "Show this help");

    final var context = new Option("c", CONTEXT_OPTION, true,
            "The context directories/files used as dependencies.");
    context.setArgs(Option.UNLIMITED_VALUES);
    options.addOption(context);
    return options;
  }

  /**
   * Load the ECSS SMP library
   *
   * @param rs
   *          the resource set to use
   */
  protected void loadEcssSmpLibrary(ResourceSet rs)
  {

    final var url = getClass().getResource("/org/eclipse/xsmp/lib/ecss.smp.xsmpcat");

    if (url == null)
    {
      LOG.fatal("Unable to load ecss.smp.xsmpcat");
      return;
    }

    final var uri = URI.createURI(url.toString());

    final var resource = resourceFactory.createResource(uri);
    try
    {
      resource.load(url.openStream(), null);
      rs.getResources().add(resource);
    }
    catch (final IOException e)
    {
      LOG.fatal("Unable to load ecss.smp.xsmpcat", e);
    }

  }

  protected void printHelp(Options options)
  {
    final var formatter = new HelpFormatter();
    formatter.printHelp("java -jar <archive>.jar",
            "Xsmpcat generator/validator Command Line Interface", options, null, true);
  }

  protected void execute(String[] args)
  {
    final var ns = System.nanoTime();
    final var options = getOptions();

    // Create a parser
    final CommandLineParser parser = new DefaultParser();

    // parse the options passed as command line arguments
    final CommandLine cmd;
    try
    {
      cmd = parser.parse(options, args);
    }
    catch (final ParseException e)
    {
      printHelp(options);
      LOG.fatal("Invalid argument", e);
      return;
    }
    if (cmd.hasOption("h"))
    {
      printHelp(options);
      return;
    }
    doExecute(cmd);
    final var df = new DecimalFormat("##.##");
    df.setRoundingMode(RoundingMode.DOWN);
    LOG.info("Executed in " + df.format((System.nanoTime() - ns) / 1000000000.) + " s");
  }

  protected void doExecute(CommandLine cmd)
  {
    final var rs = resourceSetProvider.get();

    LOG.info("Loading ECSS SMP library ... ");
    loadEcssSmpLibrary(rs);
    LOG.info("Done.");

    final var context = cmd.getOptionValues(CONTEXT_OPTION);
    if (context != null)
    {
      for (final String ctx : context)
      {
        try
        {
          Files.walkFileTree(Paths.get(ctx), new XsmpcatFileLoader(rs));
        }
        catch (final IOException e)
        {
          LOG.error("Invalid context:" + e.getLocalizedMessage());
        }
      }
    }

    // Configure output configurations
    outputConfigurationProvider.getOutputConfigurations()
            .forEach(o -> fileAccess.getOutputConfigurations().put(o.getName(), o));

    final var smdlDir = cmd.getOptionValue(SMDL_DIR_OPTION, "./smdl");

    final var validate = cmd.hasOption(VALIDATE_OPTION) || !cmd.hasOption(GENERATE_OPTION);
    final var generate = cmd.hasOption(GENERATE_OPTION) || !cmd.hasOption(VALIDATE_OPTION);

    // generate / validate files in smdl_dir
    try
    {
      Files.walkFileTree(Paths.get(smdlDir), new XsmpcatFileVisitor(rs, validate, generate));
    }
    catch (final IOException e)
    {
      LOG.fatal(e);
    }

  }

  protected class XsmpcatFileLoader extends SimpleFileVisitor<Path>
  {
    private final ResourceSet resourceSet;

    public XsmpcatFileLoader(ResourceSet resourceSet)
    {
      this.resourceSet = resourceSet;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
      final var uri = URI.createFileURI(file.toFile().getCanonicalPath());
      if (resourceServiceProvider.canHandle(uri))
      {
        // Load the context resource
        LOG.info("Loading " + uri.toFileString() + " ... ");
        resourceSet.getResource(uri, true);
        LOG.info("Done.");
      }
      return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
    {
      if (attrs.isSymbolicLink())
      {
        return FileVisitResult.SKIP_SUBTREE;
      }
      return super.preVisitDirectory(dir, attrs);
    }
  }

  protected class XsmpcatFileVisitor extends SimpleFileVisitor<Path>
  {
    private final ResourceSet resourceSet;

    private final boolean validate;

    private final boolean generate;

    public XsmpcatFileVisitor(ResourceSet resourceSet, boolean validate, boolean generate)
    {
      this.resourceSet = resourceSet;
      this.validate = validate;
      this.generate = generate;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
      final var uri = URI.createFileURI(file.toFile().getCanonicalPath());
      if (resourceServiceProvider.canHandle(uri))
      {
        // Load the resource
        final var resource = resourceSet.getResource(uri, true);
        // Validate the resource
        if (validate)
        {
          LOG.info("Validating " + uri.toFileString() + " ... ");
          final var list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
          if (!list.isEmpty())
          {
            LOG.error("Failed.");
            for (final Issue issue : list)
            {
              LOG.error(issue);
            }
            return FileVisitResult.CONTINUE;
          }
          LOG.info("Done.");
        }
        // start the generator
        if (generate)
        {
          LOG.info("Generating " + uri.toFileString() + " ... ");
          final var context = new GeneratorContext();
          context.setCancelIndicator(CancelIndicator.NullImpl);
          generator.generate(resource, fileAccess, context);
          LOG.info("Done.");
        }
      }
      return super.visitFile(file, attrs);
    }
  }
}
