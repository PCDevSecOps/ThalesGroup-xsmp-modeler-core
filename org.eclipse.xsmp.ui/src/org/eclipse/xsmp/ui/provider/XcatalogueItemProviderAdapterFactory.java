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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.xsmp.xcatalogue.util.XcatalogueAdapterFactory;

import com.google.inject.Inject;

/**
 * This is the factory that is used to provide the interfaces needed to support
 * Viewers. The adapters generated by this factory convert EMF adapter
 * notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}. The
 * adapters also support Eclipse property sheets. Note that most of the adapters
 * are shared among multiple instances.
 */
public class XcatalogueItemProviderAdapterFactory extends XcatalogueAdapterFactory
        implements ComposeableAdapterFactory, IChangeNotifier
{
  /**
   * This keeps track of the root adapter factory that delegates to this adapter
   * factory.
   */
  protected ComposedAdapterFactory parentAdapterFactory;

  /**
   * This is used to implement
   * {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
   */
  protected IChangeNotifier changeNotifier = new ChangeNotifier();

  /**
   * This keeps track of all the supported types checked by
   * {@link #isFactoryForType isFactoryForType}.
   */
  protected Collection<Object> supportedTypes = new ArrayList<>();

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Catalogue} instances.
   */
  @Inject
  protected CatalogueItemProvider catalogueItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Metadatum} instances.
   */
  @Inject
  protected MetadatumItemProvider metadatumItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Namespace} instances.
   */
  @Inject
  protected NamespaceItemProvider namespaceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Operation} instances.
   */
  @Inject
  protected OperationItemProvider operationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Attribute} instances.
   */
  @Inject
  protected AttributeItemProvider attributeItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.AttributeType} instances.
   */
  @Inject
  protected AttributeTypeItemProvider attributeTypeItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Enumeration} instances.
   */
  @Inject
  protected EnumerationItemProvider enumerationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.EnumerationLiteral} instances.
   */
  @Inject
  protected EnumerationLiteralItemProvider enumerationLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.NativeType} instances.
   */
  @Inject
  protected NativeTypeItemProvider nativeTypeItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Parameter} instances.
   */
  @Inject
  protected ParameterItemProvider parameterItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.PrimitiveType} instances.
   */
  @Inject
  protected PrimitiveTypeItemProvider primitiveTypeItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.CollectionLiteral} instances.
   */
  @Inject
  protected CollectionLiteralItemProvider collectionLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.BooleanLiteral} instances.
   */
  @Inject
  protected BooleanLiteralItemProvider booleanLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.IntegerLiteral} instances.
   */
  @Inject
  protected IntegerLiteralItemProvider integerLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.FloatingLiteral} instances.
   */
  @Inject
  protected FloatingLiteralItemProvider floatingLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.StringLiteral} instances.
   */
  @Inject
  protected StringLiteralItemProvider stringLiteralItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.ImportSection} instances.
   *
   * @since 2.7
   */
  @Inject
  protected ImportSectionItemProvider importSectionItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.ImportDeclaration} instances.
   *
   * @since 2.7
   */
  @Inject
  protected ImportDeclarationItemProvider importDeclarationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.BinaryOperation} instances.
   */
  @Inject
  protected BinaryOperationItemProvider binaryOperationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.UnaryOperation} instances.
   */
  @Inject
  protected UnaryOperationItemProvider unaryOperationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.NamedElementReference} instances.
   */
  @Inject
  protected NamedElementReferenceItemProvider enumerationLiteralReferenceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Array} instances.
   */
  @Inject
  protected ArrayItemProvider arrayItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Float} instances.
   */
  @Inject
  protected FloatItemProvider floatItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Integer} instances.
   */
  @Inject
  protected IntegerItemProvider integerItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.String} instances.
   */
  @Inject
  protected StringItemProvider stringItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.ValueReference} instances.
   */
  @Inject
  protected ValueReferenceItemProvider valueReferenceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.EventType} instances.
   */
  @Inject
  protected EventTypeItemProvider eventTypeItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Class} instances.
   */
  @Inject
  protected ClassItemProvider classItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Exception} instances.
   */
  @Inject
  protected ExceptionItemProvider exceptionItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Structure} instances.
   */
  @Inject
  protected StructureItemProvider structureItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Interface} instances.
   */
  @Inject
  protected InterfaceItemProvider interfaceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Model} instances.
   */
  @Inject
  protected ModelItemProvider modelItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Service} instances.
   */
  @Inject
  protected ServiceItemProvider serviceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Association} instances.
   */
  @Inject
  protected AssociationItemProvider associationItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Constant} instances.
   */
  @Inject
  protected ConstantItemProvider constantItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Field} instances.
   */
  @Inject
  protected FieldItemProvider fieldItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Property} instances.
   */
  @Inject
  protected PropertyItemProvider propertyItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Container} instances.
   */
  @Inject
  protected ContainerItemProvider containerItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.EntryPoint} instances.
   */
  @Inject
  protected EntryPointItemProvider entryPointItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.EventSink} instances.
   */
  @Inject
  protected EventSinkItemProvider eventSinkItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.EventSource} instances.
   */
  @Inject
  protected EventSourceItemProvider eventSourceItemProvider;

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.xsmp.xcatalogue.Reference} instances.
   */
  @Inject
  protected ReferenceItemProvider referenceItemProvider;

  /**
   * This constructs an instance.
   */
  public XcatalogueItemProviderAdapterFactory()
  {
    supportedTypes.add(IEditingDomainItemProvider.class);
    supportedTypes.add(IStructuredItemContentProvider.class);
    supportedTypes.add(ITreeItemContentProvider.class);
    supportedTypes.add(IItemLabelProvider.class);
    supportedTypes.add(IItemPropertySource.class);
    supportedTypes.add(IItemStyledLabelProvider.class);
  }

  /**
   * This implementation substitutes the factory itself as the key for the
   * adapter.
   */
  @Override
  public Adapter adapt(Notifier notifier, Object type)
  {
    return super.adapt(notifier, this);
  }

  /**
   *
   */
  @Override
  public Object adapt(Object object, Object type)
  {
    if (isFactoryForType(type))
    {
      final var adapter = super.adapt(object, type);
      if (!(type instanceof Class< ? >) || ((Class< ? >) type).isInstance(adapter))
      {
        return adapter;
      }
    }

    return null;
  }

  /**
   * This adds a listener.
   */
  @Override
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.addListener(notifyChangedListener);
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Array}.
   */
  @Override
  public Adapter createArrayAdapter()
  {
    return arrayItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.Association}.
   */
  @Override
  public Adapter createAssociationAdapter()
  {

    return associationItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Attribute}.
   */
  @Override
  public Adapter createAttributeAdapter()
  {
    return attributeItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.AttributeType}.
   */
  @Override
  public Adapter createAttributeTypeAdapter()
  {

    return attributeTypeItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.BinaryOperation}.
   */
  @Override
  public Adapter createBinaryOperationAdapter()
  {

    return binaryOperationItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.BooleanLiteral}.
   */
  @Override
  public Adapter createBooleanLiteralAdapter()
  {

    return booleanLiteralItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Catalogue}.
   */
  @Override
  public Adapter createCatalogueAdapter()
  {
    return catalogueItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Class}.
   */
  @Override
  public Adapter createClassAdapter()
  {

    return classItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.CollectionLiteral}.
   */
  @Override
  public Adapter createCollectionLiteralAdapter()
  {

    return collectionLiteralItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Constant}.
   */
  @Override
  public Adapter createConstantAdapter()
  {

    return constantItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Container}.
   */
  @Override
  public Adapter createContainerAdapter()
  {

    return containerItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.FloatingLiteral}.
   */
  @Override
  public Adapter createFloatingLiteralAdapter()
  {
    return floatingLiteralItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.EntryPoint}.
   */
  @Override
  public Adapter createEntryPointAdapter()
  {

    return entryPointItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.Enumeration}.
   */
  @Override
  public Adapter createEnumerationAdapter()
  {
    return enumerationItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.EnumerationLiteral}.
   */
  @Override
  public Adapter createEnumerationLiteralAdapter()
  {

    return enumerationLiteralItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.NamedElementReference}.
   */
  @Override
  public Adapter createNamedElementReferenceAdapter()
  {

    return enumerationLiteralReferenceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.EventSink}.
   */
  @Override
  public Adapter createEventSinkAdapter()
  {

    return eventSinkItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.EventSource}.
   */
  @Override
  public Adapter createEventSourceAdapter()
  {

    return eventSourceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.EventType}.
   */
  @Override
  public Adapter createEventTypeAdapter()
  {

    return eventTypeItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Exception}.
   */
  @Override
  public Adapter createExceptionAdapter()
  {
    return exceptionItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Field}.
   */
  @Override
  public Adapter createFieldAdapter()
  {

    return fieldItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Float}.
   */
  @Override
  public Adapter createFloatAdapter()
  {

    return floatItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.ImportDeclaration}.
   *
   * @since 2.7
   */
  @Override
  public Adapter createImportDeclarationAdapter()
  {

    return importDeclarationItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.ImportSection}.
   *
   * @since 2.7
   */
  @Override
  public Adapter createImportSectionAdapter()
  {
    return importSectionItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Integer}.
   */
  @Override
  public Adapter createIntegerAdapter()
  {

    return integerItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.IntegerLiteral}.
   */
  @Override
  public Adapter createIntegerLiteralAdapter()
  {

    return integerLiteralItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Interface}.
   */
  @Override
  public Adapter createInterfaceAdapter()
  {

    return interfaceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Metadatum}.
   */
  @Override
  public Adapter createMetadatumAdapter()
  {

    return metadatumItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Model}.
   */
  @Override
  public Adapter createModelAdapter()
  {

    return modelItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Namespace}.
   */
  @Override
  public Adapter createNamespaceAdapter()
  {
    return namespaceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.NativeType}.
   */
  @Override
  public Adapter createNativeTypeAdapter()
  {

    return nativeTypeItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Operation}.
   */
  @Override
  public Adapter createOperationAdapter()
  {
    return operationItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Parameter}.
   */
  @Override
  public Adapter createParameterAdapter()
  {

    return parameterItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.PrimitiveType}.
   */
  @Override
  public Adapter createPrimitiveTypeAdapter()
  {
    return primitiveTypeItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Property}.
   */
  @Override
  public Adapter createPropertyAdapter()
  {

    return propertyItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Reference}.
   */
  @Override
  public Adapter createReferenceAdapter()
  {
    return referenceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Service}.
   */
  @Override
  public Adapter createServiceAdapter()
  {

    return serviceItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.String}.
   */
  @Override
  public Adapter createStringAdapter()
  {

    return stringItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.StringLiteral}.
   */
  @Override
  public Adapter createStringLiteralAdapter()
  {
    return stringLiteralItemProvider;
  }

  /**
   * This creates an adapter for a {@link org.eclipse.xsmp.xcatalogue.Structure}.
   */
  @Override
  public Adapter createStructureAdapter()
  {
    return structureItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.UnaryOperation}.
   */
  @Override
  public Adapter createUnaryOperationAdapter()
  {

    return unaryOperationItemProvider;
  }

  /**
   * This creates an adapter for a
   * {@link org.eclipse.xsmp.xcatalogue.ValueReference}.
   */
  @Override
  public Adapter createValueReferenceAdapter()
  {

    return valueReferenceItemProvider;
  }

  /**
   * This delegates to {@link #changeNotifier} and to
   * {@link #parentAdapterFactory}.
   */
  @Override
  public void fireNotifyChanged(Notification notification)
  {
    changeNotifier.fireNotifyChanged(notification);

    if (parentAdapterFactory != null)
    {
      parentAdapterFactory.fireNotifyChanged(notification);
    }
  }

  /**
   * This returns the root adapter factory that contains this factory.
   */
  @Override
  public ComposeableAdapterFactory getRootAdapterFactory()
  {
    return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
  }

  /**
   *
   */
  @Override
  public boolean isFactoryForType(Object type)
  {
    return supportedTypes.contains(type) || super.isFactoryForType(type);
  }

  /**
   * This removes a listener.
   */
  @Override
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.removeListener(notifyChangedListener);
  }

  /**
   * This sets the composed adapter factory that contains this factory.
   */
  @Override
  public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
  {
    this.parentAdapterFactory = parentAdapterFactory;
  }
}
