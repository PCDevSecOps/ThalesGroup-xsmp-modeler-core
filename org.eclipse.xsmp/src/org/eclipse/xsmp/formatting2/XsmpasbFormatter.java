/*******************************************************************************
* Copyright (C) 2024 THALES ALENIA SPACE FRANCE.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.xsmp.formatting2;

import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.EMIT_GLOBAL_EVENT__NAME;
import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.LOG_ACTION__KIND_NAME;
import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.NAMED_ELEMENT__NAME;
import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.SCHEDULE__KIND;
import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.STORE_RESTORE_ACTION__STORE;
import static org.eclipse.xsmp.xcatalogue.XcataloguePackage.Literals.SUBSCRIBTION__EVENT;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsmp.services.XsmpasbGrammarAccess;
import org.eclipse.xsmp.xcatalogue.Assembly;
import org.eclipse.xsmp.xcatalogue.Call;
import org.eclipse.xsmp.xcatalogue.ComponentTypeReference;
import org.eclipse.xsmp.xcatalogue.Configuration;
import org.eclipse.xsmp.xcatalogue.Connection;
import org.eclipse.xsmp.xcatalogue.EmitGlobalEvent;
import org.eclipse.xsmp.xcatalogue.Factory;
import org.eclipse.xsmp.xcatalogue.FailureAction;
import org.eclipse.xsmp.xcatalogue.ForcibleAction;
import org.eclipse.xsmp.xcatalogue.HoldAction;
import org.eclipse.xsmp.xcatalogue.InitEntryPoint;
import org.eclipse.xsmp.xcatalogue.Instance;
import org.eclipse.xsmp.xcatalogue.InstanceDeclaration;
import org.eclipse.xsmp.xcatalogue.LoadDirective;
import org.eclipse.xsmp.xcatalogue.LogAction;
import org.eclipse.xsmp.xcatalogue.Path;
import org.eclipse.xsmp.xcatalogue.Push;
import org.eclipse.xsmp.xcatalogue.Schedule;
import org.eclipse.xsmp.xcatalogue.Simulator;
import org.eclipse.xsmp.xcatalogue.StoreRestoreAction;
import org.eclipse.xsmp.xcatalogue.Subscribtion;
import org.eclipse.xsmp.xcatalogue.Task;
import org.eclipse.xsmp.xcatalogue.Transfer;
import org.eclipse.xsmp.xcatalogue.WaitAction;
import org.eclipse.xsmp.xcatalogue.XcataloguePackage;
import org.eclipse.xtext.formatting2.IFormattableDocument;

import com.google.inject.Inject;

public class XsmpasbFormatter extends XsmpcoreFormatter
{
  @Inject
  private XsmpasbGrammarAccess ga;

  protected void format(Assembly assembly, IFormattableDocument doc)
  {
    format(assembly.getMetadatum(), doc, false);
    final var parentRegion = regionFor(assembly);
    final var kw = parentRegion.keyword(ga.getAssemblyAccess().getAssemblyKeyword_1());
    doc.prepend(kw, this::newLine);
    doc.append(kw, this::oneSpace);

    final var name = parentRegion.feature(NAMED_ELEMENT__NAME);
    doc.prepend(name, this::oneSpace);
    doc.append(name, it -> {
      it.setNewLines(3, 3, 3);
      it.lowPriority();
    });

    if (assembly.getImportSection() != null)
    {
      doc.format(assembly.getImportSection());
    }

    for (final EObject eObject : assembly.getMember())
    {
      doc.format(eObject);
      doc.append(eObject, it -> {
        it.setNewLines(2, 3, 3);
        it.lowPriority();
      });
    }
  }

  protected void format(Simulator sim, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(sim);
    final var simKw = parentRegion
            .keyword(ga.getNamespaceMemberAccess().getSimulatorKeyword_3_1_1());

    doc.append(simKw, this::oneSpace);

    doc.prepend(parentRegion.feature(NAMED_ELEMENT__NAME), this::oneSpace);

    doc.surround(parentRegion.keyword(ga.getNamespaceMemberAccess().getExtendsKeyword_3_1_3_0()),
            this::oneSpace);

    formatList(sim, XcataloguePackage.Literals.SIMULATOR__BASE, doc,
            ga.getNamespaceMemberAccess().getCommaKeyword_3_1_3_2_0());

    doc.surround(parentRegion.keyword(ga.getNamespaceMemberAccess().getEpochKeyword_3_1_4_0_0()),
            this::oneSpace);

    doc.surround(parentRegion.keyword(ga.getNamespaceMemberAccess().getMissionKeyword_3_1_4_1_0()),
            this::oneSpace);

    doc.format(sim.getEpoch());
    doc.format(sim.getMissionStart());

    format(sim.getMetadatum(), doc, false);

    formatBody(sim, doc);

  }

  protected void format(Factory factory, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(factory);

    doc.append(parentRegion.keyword(ga.getNamespaceMemberAccess().getFactoryKeyword_3_2_1()),
            this::oneSpace);

    doc.prepend(parentRegion.feature(NAMED_ELEMENT__NAME), this::oneSpace);
    doc.surround(parentRegion.keyword(ga.getNamespaceMemberAccess().getExtendsKeyword_3_2_4()),
            this::oneSpace);

    format(factory.getMetadatum(), doc, false);

    doc.format(factory.getType());

    formatBody(factory, doc);

  }

  protected void format(Configuration configuration, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(configuration);

    doc.append(parentRegion.keyword(ga.getConfigurationAccess().getSetKeyword_0()), this::oneSpace);

    doc.format(configuration.getField());
    doc.append(configuration.getField(), this::oneSpace);
    doc.format(configuration.getValue());
    doc.prepend(configuration.getValue(), this::oneSpace);
  }

  protected void format(Path path, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(path);

    parentRegion.keywords(ga.getPathAccess().getLeftSquareBracketKeyword_1_0())
            .forEach(r -> doc.surround(r, this::noSpace));
    parentRegion.keywords(ga.getPathAccess().getRightSquareBracketKeyword_1_2())
            .forEach(r -> doc.prepend(r, this::noSpace));
    path.getIndex().forEach(doc::format);

    if (path.getNext() != null)
    {
      doc.surround(parentRegion.ruleCall(ga.getPathAccess().getDotSeparatorParserRuleCall_2_0()),
              this::noSpace);
      doc.prepend(path.getNext(), this::noSpace);
      doc.format(path.getNext());
    }

  }

  protected void format(Connection connection, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(connection);

    doc.append(parentRegion.keyword(ga.getConnectionAccess().getConnectKeyword_0()),
            this::oneSpace);

    doc.format(connection.getFrom());

    doc.append(connection.getFrom(), this::oneSpace);
    doc.format(connection.getTo());
    doc.prepend(connection.getTo(), this::oneSpace);

  }

  protected void format(ComponentTypeReference ref, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(ref);

    doc.surround(
            parentRegion.keyword(ga.getComponentTypeReferenceAccess().getColonColonKeyword_1()),
            this::noSpace);

  }

  protected void format(Instance instance, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(instance);

    doc.append(parentRegion.keyword(ga.getInstanceAccess().getInstanceKeyword_0()), this::oneSpace);

    doc.format(instance.getType());
    doc.surround(parentRegion.keyword(ga.getInstanceAccess().getExtendsKeyword_2()),
            this::oneSpace);

    formatBody(instance, doc);
  }

  protected void format(InstanceDeclaration instanceDeclaration, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(instanceDeclaration);

    doc.surround(parentRegion.keyword(ga.getMemberAccess().getPlusSignEqualsSignKeyword_0_3_0_2()),
            this::oneSpace);

    doc.format(instanceDeclaration.getInstance());

    format(instanceDeclaration.getMetadatum(), doc, false);

    doc.format(instanceDeclaration.getContainer());
  }

  protected void format(LoadDirective load, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(load);

    doc.append(parentRegion.keyword(ga.getLoadDirectiveAccess().getLoadKeyword_0()),
            this::oneSpace);

  }

  protected void format(InitEntryPoint init, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(init);

    doc.append(parentRegion.keyword(ga.getInitEntryPointAccess().getInitKeyword_0()),
            this::oneSpace);

    doc.format(init.getEntryPoint());
  }

  protected void format(Subscribtion subscribtion, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(subscribtion);

    doc.append(parentRegion.keyword(ga.getSubscribtionAccess().getSubscribeKeyword_0()),
            this::oneSpace);

    doc.format(subscribtion.getEntryPoint());

    doc.prepend(parentRegion.feature(SUBSCRIBTION__EVENT), this::oneSpace);
  }

  protected void format(Call operation, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(operation);

    doc.append(parentRegion.keyword(ga.getCallAccess().getCallKeyword_0()), this::oneSpace);

    doc.format(operation.getElement());

    final var open = parentRegion.keyword(ga.getCallAccess().getLeftParenthesisKeyword_2_0());
    final var close = parentRegion.keyword(ga.getCallAccess().getRightParenthesisKeyword_2_2());
    doc.append(open, this::noSpace);
    doc.prepend(open, this::oneSpace);
    doc.prepend(close, this::noSpace);
    doc.interior(open, close, this::indent);

    formatCollection(operation.getArgument(), open, close, doc,
            ga.getCallAccess().getCommaKeyword_2_1_1_0());
  }

  protected void format(Schedule schedule, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(schedule);

    doc.append(parentRegion.keyword(ga.getScheduleAccess().getScheduleKeyword_0()), this::oneSpace);
    doc.surround(parentRegion.keyword(ga.getScheduleAccess().getCycleKeyword_2_1_2_0()),
            this::oneSpace);
    doc.surround(parentRegion.keyword(ga.getScheduleAccess().getRepeatKeyword_2_1_2_2_0()),
            this::oneSpace);

    doc.prepend(parentRegion.feature(SCHEDULE__KIND), this::oneSpace);

    if (schedule.getTime() != null)
    {
      doc.append(parentRegion.feature(SCHEDULE__KIND), this::oneSpace);

      doc.format(schedule.getTime());
    }

    doc.format(schedule.getEntryPoint());

    doc.format(schedule.getCycle());

    doc.format(schedule.getRepeat());

    doc.format(schedule.getTime());

  }

  protected void format(Push push, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(push);

    doc.append(parentRegion.keyword(ga.getPushAccess().getPushKeyword_0()), this::oneSpace);

    doc.format(push.getField());

  }

  protected void format(Transfer transfer, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(transfer);

    doc.append(parentRegion.keyword(ga.getTransferAccess().getTransferKeyword_0()), this::oneSpace);

    doc.surround(
            parentRegion.keyword(ga.getTransferAccess().getEqualsSignGreaterThanSignKeyword_2()),
            this::oneSpace);

    doc.format(transfer.getFrom());
    doc.format(transfer.getTo());

  }

  protected void format(EmitGlobalEvent emit, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(emit);

    doc.append(parentRegion.keyword(ga.getEmitGlobalEventAccess().getEmitKeyword_0()),
            this::oneSpace);

    doc.prepend(parentRegion.feature(EMIT_GLOBAL_EVENT__NAME), this::oneSpace);

    doc.prepend(parentRegion.keyword(ga.getEmitGlobalEventAccess().getAsyncAsyncKeyword_2_0()),
            this::oneSpace);

  }

  protected void format(Task task, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(task);

    doc.append(parentRegion.keyword(ga.getMemberAccess().getTaskKeyword_0_3_1_1()), this::oneSpace);

    doc.prepend(parentRegion.feature(NAMED_ELEMENT__NAME), this::oneSpace);
    formatBody(task, doc);

    format(task.getMetadatum(), doc, false);
  }

  protected void format(StoreRestoreAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    doc.append(parentRegion.feature(STORE_RESTORE_ACTION__STORE), this::oneSpace);

    doc.format(action.getFileName());

  }

  protected void format(WaitAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    final var duration = action.getDuration();
    if (duration != null)
    {
      doc.append(parentRegion.keyword(ga.getWaitActionAccess().getWaitKeyword_1()), this::oneSpace);
      doc.format(action.getDuration());
    }

  }

  protected void format(HoldAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    doc.prepend(parentRegion.keyword(ga.getHoldActionAccess().getImmediateImmediateKeyword_2_0()),
            this::oneSpace);

  }

  protected void format(FailureAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    if (action.isUnfail())
    {
      doc.append(parentRegion.keyword(ga.getFailureActionAccess().getUnfailUnfailKeyword_0_1_0()),
              this::oneSpace);
    }
    else
    {
      doc.append(parentRegion.keyword(ga.getFailureActionAccess().getFailKeyword_0_0()),
              this::oneSpace);
    }
    doc.format(action.getFailure());

  }

  protected void format(ForcibleAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    doc.format(action.getField());
    final var value = action.getValue();
    if (value != null)
    {
      doc.append(parentRegion.keyword(ga.getForcibleActionAccess().getForceKeyword_0_0()),
              this::oneSpace);
      doc.format(value);
    }
    else if (action.isUnforce())
    {
      doc.append(
              parentRegion.keyword(ga.getForcibleActionAccess().getUnforceUnforceKeyword_1_0_1_0()),
              this::oneSpace);
    }
    else
    {
      doc.append(parentRegion.keyword(ga.getForcibleActionAccess().getFreezeKeyword_1_0_0()),
              this::oneSpace);
    }

  }

  protected void format(LogAction action, IFormattableDocument doc)
  {
    final var parentRegion = regionFor(action);

    doc.prepend(parentRegion.feature(LOG_ACTION__KIND_NAME), this::oneSpace);
    doc.prepend(action.getMessage(), this::oneSpace);
    doc.format(action.getMessage());

  }
}
