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
package org.eclipse.xsmp.ui.provider;

import java.util.List;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.xsmp.xcatalogue.XcataloguePackage;

import com.google.inject.Inject;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.xsmp.xcatalogue.EntryPoint} object.
 */
public class EntryPointItemProvider extends NamedElementItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   */
  @Inject
  public EntryPointItemProvider(XcatalogueItemProviderAdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addInputPropertyDescriptor(object);
      addOutputPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Input feature.
   */
  protected void addInputPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
            ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_EntryPoint_input_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_EntryPoint_input_feature",
                    "_UI_EntryPoint_type"),
            XcataloguePackage.Literals.ENTRY_POINT__INPUT, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Output feature.
   */
  protected void addOutputPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
            ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_EntryPoint_output_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_EntryPoint_output_feature",
                    "_UI_EntryPoint_type"),
            XcataloguePackage.Literals.ENTRY_POINT__OUTPUT, true, false, true, null, null, null));
  }

}
