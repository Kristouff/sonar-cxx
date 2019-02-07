/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2018 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.cxx.cfamily;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.*;
import org.sonar.cxx.sensors.coverage.CxxCoverageCache;
import org.sonar.cxx.sensors.coverage.CxxCoverageSensor;
import org.sonar.cxx.sensors.cppcheck.CxxCppCheckRuleRepository;
import org.sonar.cxx.sensors.cppcheck.CxxCppCheckSensor;
import org.sonar.cxx.sensors.squid.CustomCxxRulesDefinition;
import org.sonar.cxx.sensors.squid.CxxSquidSensor;
import org.sonar.cxx.sensors.tests.xunit.CxxXunitSensor;
import org.sonar.cxx.sensors.valgrind.CxxValgrindRuleRepository;
import org.sonar.cxx.sensors.valgrind.CxxValgrindSensor;

/**
 * {@inheritDoc}
 */
public final class CxxCFamilyPlugin implements Plugin {

  private static final Logger LOGGER = Loggers.get(CxxCFamilyPlugin.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public void define(Context context) {
    List<Object> l = new ArrayList<>();

    // Sonar CXX Analyzer
    // Instantiating rule repository causes runtime exception:
    // org.picocontainer.injectors.AbstractInjector$UnsatisfiableDependenciesException: org.sonar.plugins.cxx.cfamily.CxxCFamilyRuleRepository has unsatisfied dependency 'class org.sonar.cxx.CxxLanguage' for constructor 'public org.sonar.plugins.cxx.cfamily.CxxCFamilyRuleRepository(org.sonar.cxx.CxxLanguage)' from org.sonar.core.platform.ComponentContainer$ExtendedDefaultPicoContainer@56ddb7d9:800<[Immutable]:org.sonar.core.platform.ComponentContainer$ExtendedDefaultPicoContainer@7384cdfd:15<[Immutable]:org.sonar.core.platform.ComponentContainer$ExtendedDefaultPicoContainer@63526937:33<[Immutable]:org.sonar.core.platform.ComponentContainer$ExtendedDefaultPicoContainer@2b145e65:162<
    // l.add(CxxCFamilyRuleRepository.class);
    // l.add(CxxSquidSensorImpl.class);

    // CppCheck
    l.add(CxxCppCheckSensorImpl.class);
    l.add(CxxCppCheckRuleRepositoryImpl.class);
    // Valgrind
    l.add(CxxValgrindSensorImpl.class);
    l.add(CxxValgrindRuleRepositoryImpl.class);
    // Coverage
    l.add(CxxCoverageSensorImpl.class);
    l.add(CxxCoverageAggregator.class);

    context.addExtensions(l);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  // public static class CxxSquidSensorImpl extends CxxSquidSensor {

  //   public CxxSquidSensorImpl(Configuration settings,
  //     FileLinesContextFactory fileLinesContextFactory,
  //     CheckFactory checkFactory) {
  //     super(new CxxCFamilyLanguage(settings), fileLinesContextFactory, checkFactory);
  //   }

  //   public CxxSquidSensorImpl(Configuration settings,
  //     FileLinesContextFactory fileLinesContextFactory,
  //     CheckFactory checkFactory,
  //     @Nullable CustomCxxRulesDefinition[] customRulesDefinition) {
  //     super(new CxxCFamilyLanguage(settings), fileLinesContextFactory, checkFactory, customRulesDefinition);
  //   }
  // }

    public static class CxxCppCheckSensorImpl extends CxxCppCheckSensor {

    public CxxCppCheckSensorImpl(Configuration settings) {
      super(new CxxCFamilyLanguage(settings));
    }
  }

  public static class CxxCppCheckRuleRepositoryImpl extends CxxCppCheckRuleRepository {

    public CxxCppCheckRuleRepositoryImpl(ServerFileSystem fileSystem, RulesDefinitionXmlLoader xmlRuleLoader,
      Configuration settings) {
      super(fileSystem, xmlRuleLoader, new CxxCFamilyLanguage(settings));
    }
  }

  public static class CxxValgrindSensorImpl extends CxxValgrindSensor {

    public CxxValgrindSensorImpl(Configuration settings) {
      super(new CxxCFamilyLanguage(settings));
    }
  }

  public static class CxxValgrindRuleRepositoryImpl extends CxxValgrindRuleRepository {

    public CxxValgrindRuleRepositoryImpl(ServerFileSystem fileSystem, RulesDefinitionXmlLoader xmlRuleLoader,
      Configuration settings) {
      super(fileSystem, xmlRuleLoader, new CxxCFamilyLanguage(settings));
    }
  }

  public static class CxxCoverageSensorImpl extends CxxCoverageSensor {

    public CxxCoverageSensorImpl(Configuration settings, CxxCoverageAggregator cache, SensorContext context) {
      super(cache, new CxxCFamilyLanguage(settings), context);
    }
  }

  public static class CxxCoverageAggregator extends CxxCoverageCache {

    public CxxCoverageAggregator() {
      super();
    }
  }
}
