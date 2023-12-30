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
package org.eclipse.xsmp.xcatalogue.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.xsmp.xcatalogue.XcatalogueFactory;
import org.eclipse.xsmp.xcatalogue.XcataloguePackage;

public class SimulatorImplCustom extends SimulatorImpl
{

  protected SimulatorImplCustom()
  {
    initModels();
    initServices();
  }

  private void initModels()
  {

    final var newModels = XcatalogueFactory.eINSTANCE.createContainer();

    newModels.setName("Models");
    newModels.setMultiplicity(XcatalogueFactory.eINSTANCE.createMultiplicity());
    newModels.setDescription("Models collection of the simulator");

    NotificationChain msgs = null;

    msgs = ((InternalEObject) newModels).eInverseAdd(this,
            EOPPOSITE_FEATURE_BASE - XcataloguePackage.SIMULATOR__MODELS, null, msgs);

    msgs = basicSetModels(newModels, msgs);
    if (msgs != null)
    {
      msgs.dispatch();
    }

  }

  private void initServices()
  {

    final var newServices = XcatalogueFactory.eINSTANCE.createContainer();

    newServices.setName("Services");
    newServices.setDescription("Services collection of the simulator");
    newServices.setMultiplicity(XcatalogueFactory.eINSTANCE.createMultiplicity());

    NotificationChain msgs = null;

    msgs = ((InternalEObject) newServices).eInverseAdd(this,
            EOPPOSITE_FEATURE_BASE - XcataloguePackage.SIMULATOR__SERVICES, null, msgs);

    msgs = basicSetServices(newServices, msgs);
    if (msgs != null)
    {
      msgs.dispatch();
    }

  }

} // SimulatorImplCustom
