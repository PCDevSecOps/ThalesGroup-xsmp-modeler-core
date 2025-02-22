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
package org.eclipse.xsmp.tool.smp.generator;

import static org.eclipse.xsmp.tool.smp.generator.SmpOutputConfigurationProvider.SMDL_GEN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsmp.documentation.TextElement;
import org.eclipse.xsmp.tool.smp.core.elements.ElementsFactory;
import org.eclipse.xsmp.tool.smp.core.types.AccessKind;
import org.eclipse.xsmp.tool.smp.core.types.ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.BoolArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.BoolValue;
import org.eclipse.xsmp.tool.smp.core.types.Char8ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Char8Value;
import org.eclipse.xsmp.tool.smp.core.types.DateTimeArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.DateTimeValue;
import org.eclipse.xsmp.tool.smp.core.types.DurationArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.DurationValue;
import org.eclipse.xsmp.tool.smp.core.types.ElementReference;
import org.eclipse.xsmp.tool.smp.core.types.EnumerationArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.EnumerationValue;
import org.eclipse.xsmp.tool.smp.core.types.Float32ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Float32Value;
import org.eclipse.xsmp.tool.smp.core.types.Float64ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Float64Value;
import org.eclipse.xsmp.tool.smp.core.types.Int16ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Int16Value;
import org.eclipse.xsmp.tool.smp.core.types.Int32ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Int32Value;
import org.eclipse.xsmp.tool.smp.core.types.Int64ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Int64Value;
import org.eclipse.xsmp.tool.smp.core.types.Int8ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.Int8Value;
import org.eclipse.xsmp.tool.smp.core.types.Parameter;
import org.eclipse.xsmp.tool.smp.core.types.ParameterDirectionKind;
import org.eclipse.xsmp.tool.smp.core.types.SimpleValue;
import org.eclipse.xsmp.tool.smp.core.types.String8ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.String8Value;
import org.eclipse.xsmp.tool.smp.core.types.TypesFactory;
import org.eclipse.xsmp.tool.smp.core.types.UInt16ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.UInt16Value;
import org.eclipse.xsmp.tool.smp.core.types.UInt32ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.UInt32Value;
import org.eclipse.xsmp.tool.smp.core.types.UInt64ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.UInt64Value;
import org.eclipse.xsmp.tool.smp.core.types.UInt8ArrayValue;
import org.eclipse.xsmp.tool.smp.core.types.UInt8Value;
import org.eclipse.xsmp.tool.smp.core.types.Value;
import org.eclipse.xsmp.tool.smp.core.types.VisibilityKind;
import org.eclipse.xsmp.tool.smp.smdl.catalogue.Interface;
import org.eclipse.xsmp.tool.smp.smdl.package_.PackageFactory;
import org.eclipse.xsmp.tool.smp.util.SmpURIConverter;
import org.eclipse.xsmp.util.XsmpUtil;
import org.eclipse.xsmp.xcatalogue.Array;
import org.eclipse.xsmp.xcatalogue.Association;
import org.eclipse.xsmp.xcatalogue.Attribute;
import org.eclipse.xsmp.xcatalogue.AttributeType;
import org.eclipse.xsmp.xcatalogue.Catalogue;
import org.eclipse.xsmp.xcatalogue.Class;
import org.eclipse.xsmp.xcatalogue.CollectionLiteral;
import org.eclipse.xsmp.xcatalogue.Component;
import org.eclipse.xsmp.xcatalogue.Constant;
import org.eclipse.xsmp.xcatalogue.Container;
import org.eclipse.xsmp.xcatalogue.DesignatedInitializer;
import org.eclipse.xsmp.xcatalogue.Document;
import org.eclipse.xsmp.xcatalogue.EntryPoint;
import org.eclipse.xsmp.xcatalogue.Enumeration;
import org.eclipse.xsmp.xcatalogue.EnumerationLiteral;
import org.eclipse.xsmp.xcatalogue.EventSink;
import org.eclipse.xsmp.xcatalogue.EventSource;
import org.eclipse.xsmp.xcatalogue.EventType;
import org.eclipse.xsmp.xcatalogue.Expression;
import org.eclipse.xsmp.xcatalogue.Field;
import org.eclipse.xsmp.xcatalogue.Float;
import org.eclipse.xsmp.xcatalogue.Integer;
import org.eclipse.xsmp.xcatalogue.NamedElement;
import org.eclipse.xsmp.xcatalogue.Namespace;
import org.eclipse.xsmp.xcatalogue.NativeType;
import org.eclipse.xsmp.xcatalogue.Operation;
import org.eclipse.xsmp.xcatalogue.Property;
import org.eclipse.xsmp.xcatalogue.Reference;
import org.eclipse.xsmp.xcatalogue.ReferenceType;
import org.eclipse.xsmp.xcatalogue.Structure;
import org.eclipse.xsmp.xcatalogue.Type;
import org.eclipse.xsmp.xcatalogue.ValueReference;
import org.eclipse.xsmp.xcatalogue.VisibilityElement;
import org.eclipse.xsmp.xcatalogue.XcataloguePackage;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;

/**
 * @author daveluy
 */
public class SmpGenerator extends AbstractModelConverter
{

  @Inject
  private SmpURIConverter smpURIConverter;

  @Inject
  private IResourceScopeCache cache;

  @Inject
  private XsmpUtil xsmpUtil;

  private static final String SMPCAT_EXT = "smpcat";

  private static final String SMPPKG_EXT = "smppkg";

  @Override
  public void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context)
  {
    final var cat = (Catalogue) resource.getContents().get(0);

    // Resource set for the smpcat/smppkg resources
    final var rs = new ResourceSetImpl();
    rs.setURIConverter(smpURIConverter);

    // Generate the SMP catalogue
    final var generatedCatalogue = (org.eclipse.xsmp.tool.smp.smdl.catalogue.Catalogue) copy(cat);

    // Save the catalogue resource
    final var catFileName = resource.getURI().trimFileExtension().appendFileExtension(SMPCAT_EXT)
            .lastSegment();

    final var catalogueResource = rs.createResource(fsa.getURI(catFileName, SMDL_GEN));
    catalogueResource.getContents().add(generatedCatalogue);
    try
    {
      final var os = new ByteArrayOutputStream();
      catalogueResource.save(os, Collections.emptyMap());

      fsa.generateFile(catFileName, SMDL_GEN, os.toString(StandardCharsets.UTF_8));
    }
    catch (final IOException e)
    {
      // ignore
    }

    // Generate the SMP package
    final var pkg = PackageFactory.eINSTANCE.createPackage();

    pkg.setName(generatedCatalogue.getName());
    pkg.setCreator(generatedCatalogue.getCreator());
    pkg.setDate(generatedCatalogue.getDate());
    pkg.setTitle(generatedCatalogue.getTitle());
    pkg.setVersion(generatedCatalogue.getVersion());
    pkg.setDescription(generatedCatalogue.getDescription());

    pkg.setId(id(cat));

    // Add all implementations of the package
    generatedCatalogue.eAllContents().forEachRemaining(it -> {
      if (it instanceof final org.eclipse.xsmp.tool.smp.core.types.Type type
              && !(it instanceof Interface))
      {
        final var impl = PackageFactory.eINSTANCE.createImplementationOfPackage();
        impl.setType(type);
        impl.setTypeName(type.getName());
        pkg.getImplementation().add(impl);
      }
    });

    // Add all the dependencies of the package
    for (final Catalogue catalogue : xsmpUtil.dependentPackages(cat))
    {
      final var dep = PackageFactory.eINSTANCE.createDependencyOfPackage();
      dep.setPackage(PackageFactory.eINSTANCE.createPackage());
      ((InternalEObject) dep.getPackage()).eSetProxyURI(toSmppkgURI(EcoreUtil.getURI(catalogue)));
      dep.setPackageName(catalogue.getName());
      pkg.getDependency().add(dep);
    }

    // Save the package resource

    final var pkgFileName = resource.getURI().trimFileExtension().appendFileExtension(SMPPKG_EXT)
            .lastSegment();
    final var packageResource = rs.createResource(fsa.getURI(pkgFileName, SMDL_GEN));
    packageResource.getContents().add(pkg);
    try
    {
      final var os = new ByteArrayOutputStream();
      packageResource.save(os, Collections.emptyMap());

      fsa.generateFile(pkgFileName, SMDL_GEN, os.toString(StandardCharsets.UTF_8));
    }
    catch (final IOException e)
    {
      // ignore
    }

  }

  /* --- id --- */

  private String id(EObject e)
  {
    return cache.get("smp_id_map", e.eResource(), () -> loadSmpIds(e.eResource())).get(e);
  }

  /**
   * Computes the fully qualified name for the given object, if any.
   */
  private Map<EObject, String> loadSmpIds(Resource resource)
  {
    final Map<String, EObject> idToEObjectMap = new HashMap<>();

    final Map<EObject, String> eObjectToIdMap = new HashMap<>();
    resource.getAllContents().forEachRemaining(e -> {

      if (e instanceof final NamedElement elem && !eObjectToIdMap.containsKey(e))
      {
        final var idTag = elem.getMetadatum().getXsmpcatdoc().tags().stream()
                .filter(t -> "@id".equals(t.getTagName())).findFirst();

        String id;
        if (idTag.isPresent())
        {
          // get the id from the @id tag in the description
          id = idTag.get().fragments().stream().map(TextElement::getText)
                  .collect(Collectors.joining("\n")).trim();
        }
        else
        {
          // compute the id manually

          id = ((NamedElement) e).getName();
          // Prefix the document ID with _ to avoid ID conflict in case of a namespace
          // with the same
          // name than the document
          if (e instanceof Document)
          {
            id = "_" + id;
          }
          else
          {
            final var container = EcoreUtil2.getContainerOfType(e.eContainer(), NamedElement.class);

            if (!(container instanceof Document))
            {
              id = eObjectToIdMap.get(container) + "." + id;
            }
          }
        }
        // in case of ID conflict (e.g: operations with same name but different
        // signature),
        // increment the ID
        if (idToEObjectMap.containsKey(id))
        {
          var i = 1;
          String newId;
          do
          {
            i++;
            newId = id + i;
          }
          while (idToEObjectMap.containsKey(newId));

          id = newId;
        }

        idToEObjectMap.put(id, e);
        eObjectToIdMap.put(e, id);

        for (final var attribute : ((NamedElement) e).getMetadatum().getMetadata())
        {
          var attributeId = id + "." + attribute.getType().getName();
          if (idToEObjectMap.containsKey(attributeId))
          {
            var i = 1;
            String newId;
            do
            {
              i++;
              newId = attributeId + i;
            }
            while (idToEObjectMap.containsKey(newId));

            attributeId = newId;
          }

          idToEObjectMap.put(attributeId, attribute);
          eObjectToIdMap.put(attribute, attributeId);
        }

      }
    });
    return eObjectToIdMap;
  }

  /* --- getReference --- */

  private <T extends org.eclipse.xsmp.tool.smp.core.elements.NamedElement> ElementReference<T> getReference(
          NamedElement src, Resource resource)
  {
    final ElementReference<T> ref = TypesFactory.eINSTANCE.createElementReference();

    ref.setTitle(src.getName());
    ref.setReference(ElementsFactory.eINSTANCE.createNamedElement());
    final var srcResource = src.eResource();
    if (srcResource == resource)
    {
      ((InternalEObject) ref.getReference()).eSetProxyURI(URI.createURI("#" + id(src)));
    }
    else if (srcResource == null)
    {
      ((InternalEObject) ref.getReference())
              .eSetProxyURI(URI.createFileURI("<unresolved>.smpcat").appendFragment(id(src)));
    }
    else
    {
      ((InternalEObject) ref.getReference())
              .eSetProxyURI(toSmpcatURI(srcResource.getURI()).appendFragment(id(src)));
    }

    return ref;
  }

  /* --- toSmp... --- */

  private URI toSmpcatURI(URI uri)
  {
    if ("ecss.smp.xsmpcat".equals(uri.lastSegment()))
    {
      return URI.createURI("http://www.ecss.nl/smp/2019/Smdl");
    }
    return toSmpcatGenURI(uri);
  }

  private URI toSmpcatGenURI(URI uri)
  {
    final var lastSeg = uri.trimFileExtension().appendFileExtension(SMPCAT_EXT).lastSegment();
    return URI.createFileURI(lastSeg);
  }

  private URI toSmppkgURI(URI uri)
  {
    return toSmppkgGenURI(uri);
  }

  private URI toSmppkgGenURI(URI uri)
  {
    final var lastSeg = uri.trimFileExtension().appendFileExtension(SMPPKG_EXT).lastSegment();
    return URI.createFileURI(lastSeg);
  }

  protected void copy(NamedElement src, org.eclipse.xsmp.tool.smp.core.elements.NamedElement dest)
  {
    dest.setName(src.getName());

    if (src.getDescription() != null)
    {
      dest.setDescription(src.getDescription().trim());
    }

    dest.setId(id(src));

    src.getMetadatum().getMetadata().stream().forEach(
            m -> dest.getMetadata().add((org.eclipse.xsmp.tool.smp.core.types.Attribute) copy(m)));
  }

  protected void copy(Attribute src, org.eclipse.xsmp.tool.smp.core.types.Attribute dest)
  {
    dest.setName(src.getType().getName());
    dest.setId(id(src));

    final var type = (AttributeType) src.getType();
    dest.setType(getReference(type, src.eResource()));

    if (src.getValue() != null)
    {
      dest.setValue(convert(src.getValue()));
    }
    else
    {
      dest.setValue(convert(type.getDefault()));
    }
  }

  protected void copy(EnumerationLiteral src,
          org.eclipse.xsmp.tool.smp.core.types.EnumerationLiteral dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);
    dest.setValue(xsmpUtil.getInt32(src.getValue()));
  }

  protected void copy(Document src, org.eclipse.xsmp.tool.smp.core.elements.Document dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setCreator(src.getCreator().stream().collect(Collectors.joining(", ")));
    final var date = src.getDate();
    if (date != null)
    {
      final var gc = new GregorianCalendar();
      gc.setTime(src.getDate());
      gc.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
      try
      {
        dest.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      }
      catch (final Exception e)
      {
        // ignore
      }
    }
    dest.setTitle(src.getTitle());
    dest.setVersion(src.getVersion());
  }

  protected void copy(Catalogue src, org.eclipse.xsmp.tool.smp.smdl.catalogue.Catalogue dest)
  {
    copy((Document) src, (org.eclipse.xsmp.tool.smp.core.elements.Document) dest);

    src.getMember().stream().filter(Namespace.class::isInstance).forEach(m -> dest.getNamespace()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.Namespace) copy(m)));
  }

  protected void copy(Namespace src, org.eclipse.xsmp.tool.smp.smdl.catalogue.Namespace dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    src.getMember().stream().filter(Namespace.class::isInstance).forEach(m -> dest.getNamespace()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.Namespace) copy(m)));

    src.getMember().stream().filter(Type.class::isInstance)
            .forEach(m -> dest.getType().add((org.eclipse.xsmp.tool.smp.core.types.Type) copy(m)));
  }

  protected void copy(VisibilityElement src,
          org.eclipse.xsmp.tool.smp.core.types.VisibilityElement dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    if (src.isSetVisibility())
    {
      dest.setVisibility(VisibilityKind.get(src.getRealVisibility().getLiteral()));
    }
  }

  protected void copy(Type src, org.eclipse.xsmp.tool.smp.core.types.Type dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    dest.setUuid(src.getUuid());
  }

  protected void copy(Array src, org.eclipse.xsmp.tool.smp.core.types.Array dest)
  {
    copy((Type) src, dest);

    dest.setSize(xsmpUtil.getInt64(src.getSize()));
    dest.setItemType(getReference(src.getItemType(), src.eResource()));
  }

  protected void copy(Float src, org.eclipse.xsmp.tool.smp.core.types.Float dest)
  {
    copy((Type) src, dest);

    if (src.getMaximum() != null)
    {
      dest.setMaximum(xsmpUtil.getFloat64(src.getMaximum()));
    }
    dest.setMaxInclusive(src.isMaxInclusive());

    if (src.getMinimum() != null)
    {
      dest.setMinimum(xsmpUtil.getFloat64(src.getMinimum()));
    }
    dest.setMinInclusive(src.isMinInclusive());
    dest.setUnit(src.getUnit());

    if (src.getPrimitiveType() != null)
    {
      dest.setPrimitiveType(getReference(src.getPrimitiveType(), src.eResource()));
    }
  }

  protected void copy(Integer src, org.eclipse.xsmp.tool.smp.core.types.Integer dest)
  {
    copy((Type) src, dest);

    if (src.getMaximum() != null)
    {
      dest.setMaximum(xsmpUtil.getInt64(src.getMaximum()));
    }

    if (src.getMinimum() != null)
    {
      dest.setMinimum(xsmpUtil.getInt64(src.getMinimum()));
    }

    dest.setUnit(src.getUnit());

    if (src.getPrimitiveType() != null)
    {
      dest.setPrimitiveType(getReference(src.getPrimitiveType(), src.eResource()));
    }
  }

  protected void copy(org.eclipse.xsmp.xcatalogue.String src,
          org.eclipse.xsmp.tool.smp.core.types.String dest)
  {
    copy((Type) src, dest);

    dest.setLength(xsmpUtil.getInt64(src.getLength()));
  }

  protected void copy(Structure src, org.eclipse.xsmp.tool.smp.core.types.Structure dest)
  {
    copy((Type) src, (org.eclipse.xsmp.tool.smp.core.types.Type) dest);

    src.getMember().stream().filter(Constant.class::isInstance).forEach(cst -> dest.getConstant()
            .add((org.eclipse.xsmp.tool.smp.core.types.Constant) copy(cst)));

    src.getMember().stream().filter(Field.class::isInstance).forEach(
            f -> dest.getField().add((org.eclipse.xsmp.tool.smp.core.types.Field) copy(f)));
  }

  protected void copy(Enumeration src, org.eclipse.xsmp.tool.smp.core.types.Enumeration dest)
  {
    copy((Type) src, (org.eclipse.xsmp.tool.smp.core.types.Type) dest);

    src.getLiteral().forEach(l -> dest.getLiteral()
            .add((org.eclipse.xsmp.tool.smp.core.types.EnumerationLiteral) copy(l)));
  }

  protected void copy(ReferenceType src,
          org.eclipse.xsmp.tool.smp.smdl.catalogue.ReferenceType dest)
  {
    copy((Type) src, (org.eclipse.xsmp.tool.smp.core.types.Type) dest);

    src.getMember().stream().filter(Constant.class::isInstance).forEach(cst -> dest.getConstant()
            .add((org.eclipse.xsmp.tool.smp.core.types.Constant) copy(cst)));

    src.getMember().stream().filter(Property.class::isInstance).forEach(
            p -> dest.getProperty().add((org.eclipse.xsmp.tool.smp.core.types.Property) copy(p)));

    src.getMember().stream().filter(Operation.class::isInstance).forEach(
            o -> dest.getOperation().add((org.eclipse.xsmp.tool.smp.core.types.Operation) copy(o)));
  }

  protected void copy(Component src, org.eclipse.xsmp.tool.smp.smdl.catalogue.Component dest)
  {
    copy((ReferenceType) src, (org.eclipse.xsmp.tool.smp.smdl.catalogue.ReferenceType) dest);

    if (src.getBase() != null)
    {
      dest.setBase(getReference(src.getBase(), src.eResource()));
    }

    src.getInterface().forEach(i -> dest.getInterface().add(getReference(i, src.eResource())));

    src.getMember().stream().filter(EntryPoint.class::isInstance).forEach(a -> dest.getEntryPoint()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.EntryPoint) copy(a)));

    src.getMember().stream().filter(EventSink.class::isInstance).forEach(a -> dest.getEventSink()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.EventSink) copy(a)));

    src.getMember().stream().filter(EventSource.class::isInstance).forEach(a -> dest
            .getEventSource().add((org.eclipse.xsmp.tool.smp.smdl.catalogue.EventSource) copy(a)));

    src.getMember().stream().filter(Field.class::isInstance).forEach(
            a -> dest.getField().add((org.eclipse.xsmp.tool.smp.core.types.Field) copy(a)));

    src.getMember().stream().filter(Association.class::isInstance).forEach(a -> dest
            .getAssociation().add((org.eclipse.xsmp.tool.smp.core.types.Association) copy(a)));

    src.getMember().stream().filter(Container.class::isInstance).forEach(a -> dest.getContainer()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.Container) copy(a)));

    src.getMember().stream().filter(Reference.class::isInstance).forEach(a -> dest.getReference()
            .add((org.eclipse.xsmp.tool.smp.smdl.catalogue.Reference) copy(a)));
  }

  protected void copy(org.eclipse.xsmp.xcatalogue.Interface src, Interface dest)
  {
    copy((ReferenceType) src, (org.eclipse.xsmp.tool.smp.smdl.catalogue.ReferenceType) dest);

    src.getBase().forEach(i -> dest.getBase().add(getReference(i, src.eResource())));
  }

  protected void copy(Operation src, org.eclipse.xsmp.tool.smp.core.types.Operation dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    src.getParameter().forEach(p -> dest.getParameter().add((Parameter) copy(p)));

    if (src.getReturnParameter() != null)
    {
      final var cpy = (Parameter) copy(src.getReturnParameter());

      dest.getParameter().add(cpy);
    }

    src.getRaisedException()
            .forEach(e -> dest.getRaisedException().add(getReference(e, src.eResource())));
  }

  protected void copy(Class src, org.eclipse.xsmp.tool.smp.core.types.Class dest)
  {
    copy((Structure) src, (org.eclipse.xsmp.tool.smp.core.types.Structure) dest);

    dest.setAbstract(src.isAbstract());

    if (src.getBase() != null)
    {
      dest.setBase(getReference(src.getBase(), src.eResource()));
    }

    src.getMember().stream().filter(Association.class::isInstance).forEach(a -> dest
            .getAssociation().add((org.eclipse.xsmp.tool.smp.core.types.Association) copy(a)));

    src.getMember().stream().filter(Property.class::isInstance).forEach(
            a -> dest.getProperty().add((org.eclipse.xsmp.tool.smp.core.types.Property) copy(a)));

    src.getMember().stream().filter(Operation.class::isInstance).forEach(
            a -> dest.getOperation().add((org.eclipse.xsmp.tool.smp.core.types.Operation) copy(a)));
  }

  protected void copy(Constant src, org.eclipse.xsmp.tool.smp.core.types.Constant dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    dest.setValue((SimpleValue) convert(src.getValue()));

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(AttributeType src, org.eclipse.xsmp.tool.smp.core.types.AttributeType dest)
  {
    copy((Type) src, (org.eclipse.xsmp.tool.smp.core.types.Type) dest);

    dest.getUsage().addAll(src.getUsage());

    dest.setAllowMultiple(src.isAllowMultiple());

    if (src.getDefault() != null)
    {
      dest.setDefault(convert(src.getDefault()));
    }

    dest.setType(getReference(src.getType(), src.eResource()));

  }

  protected void copy(Field src, org.eclipse.xsmp.tool.smp.core.types.Field dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    if (src.getDefault() != null)
    {
      dest.setDefault(convert(src.getDefault()));
    }

    dest.setInput(src.isInput());
    dest.setOutput(src.isOutput());
    dest.setState(!src.isTransient());

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(org.eclipse.xsmp.xcatalogue.Parameter src, Parameter dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setDirection(ParameterDirectionKind.get(src.getDirection().ordinal()));

    if (src.getDefault() != null)
    {
      dest.setDefault(convert(src.getDefault()));
    }

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(Property src, org.eclipse.xsmp.tool.smp.core.types.Property dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    dest.setCategory(src.getCategory());
    dest.setAccess(AccessKind.get(src.getAccess().ordinal()));

    dest.setType(getReference(src.getType(), src.eResource()));

    if (src.getAttachedField() != null)
    {
      dest.setAttachedField(getReference(src.getAttachedField(), src.eResource()));
    }

    src.getGetRaises().forEach(r -> dest.getGetRaises().add(getReference(r, src.eResource())));
    src.getSetRaises().forEach(r -> dest.getGetRaises().add(getReference(r, src.eResource())));
  }

  protected void copy(Container src, org.eclipse.xsmp.tool.smp.smdl.catalogue.Container dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setLower(src.getLower());
    dest.setUpper(src.getUpper());

    dest.setType(getReference(src.getType(), src.eResource()));

    if (src.getDefaultComponent() != null)
    {
      dest.setDefaultComponent(getReference(src.getDefaultComponent(), src.eResource()));
    }
  }

  protected void copy(Reference src, org.eclipse.xsmp.tool.smp.smdl.catalogue.Reference dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setLower(src.getLower());
    dest.setUpper(src.getUpper());
    dest.setInterface(getReference(src.getInterface(), src.eResource()));
  }

  protected void copy(EventSource src, org.eclipse.xsmp.tool.smp.smdl.catalogue.EventSource dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setMulticast(!src.isSinglecast());
    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(ValueReference src, org.eclipse.xsmp.tool.smp.core.types.ValueReference dest)
  {
    copy((Type) src, dest);

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(EventType src, org.eclipse.xsmp.tool.smp.smdl.catalogue.EventType dest)
  {
    copy((Type) src, dest);

    if (src.getEventArgs() != null)
    {
      dest.setEventArgs(getReference(src.getEventArgs(), src.eResource()));
    }
  }

  protected void copy(NativeType src, org.eclipse.xsmp.tool.smp.core.types.NativeType dest)
  {
    copy((Type) src, dest);

    final var mapping = TypesFactory.eINSTANCE.createPlatformMapping();
    mapping.setName("cpp");
    mapping.setLocation(src.getLocation());
    mapping.setNamespace(src.getNamespace());
    mapping.setType(src.getType());
    dest.getPlatform().add(mapping);
  }

  protected void copy(Association src, org.eclipse.xsmp.tool.smp.core.types.Association dest)
  {
    copy((VisibilityElement) src, (org.eclipse.xsmp.tool.smp.core.types.VisibilityElement) dest);

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  protected void copy(EntryPoint src, org.eclipse.xsmp.tool.smp.smdl.catalogue.EntryPoint dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    src.getInput().forEach(r -> dest.getInput().add(getReference(r, src.eResource())));
    src.getOutput().forEach(r -> dest.getOutput().add(getReference(r, src.eResource())));
  }

  protected void copy(EventSink src, org.eclipse.xsmp.tool.smp.smdl.catalogue.EventSink dest)
  {
    copy((NamedElement) src, (org.eclipse.xsmp.tool.smp.core.elements.NamedElement) dest);

    dest.setType(getReference(src.getType(), src.eResource()));
  }

  private Value convert(Expression value)
  {
    final var type = xsmpUtil.getType(value);

    final var primitiveType = xsmpUtil.getPrimitiveTypeKind(type);

    try
    {
      switch (primitiveType)
      {
        case BOOL:
          return boolValue(value);
        case CHAR8:
          return char8Value(value);
        case DATE_TIME:
          return dateTimeValue(value);
        case DURATION:
          return durationValue(value);
        case FLOAT32:
          return float32Value(value);
        case FLOAT64:
          return float64Value(value);
        case INT16:
          return int16Value(value);
        case INT32:
          return int32Value(value);
        case INT64:
          return int64Value(value);
        case INT8:
          return int8Value(value);
        case STRING8:
          return string8Value(value);
        case UINT16:
          return uint16Value(value);
        case UINT32:
          return uint32Value(value);
        case UINT64:
          return uint64Value(value);
        case UINT8:
          return uint8Value(value);
        case ENUM:
          return enumValue(value);
        case NONE:
          switch (type.eClass().getClassifierID())
          {
            case XcataloguePackage.ARRAY:
              return convert((CollectionLiteral) value, (Array) type);
            case XcataloguePackage.STRUCTURE, XcataloguePackage.CLASS, XcataloguePackage.EXCEPTION:
              return convert((CollectionLiteral) value);
            default:
              break;
          }
          break;
        default:
          break;
      }
    }
    catch (final Exception e)
    {
      // ignore
    }

    return null;
  }

  private Value convert(CollectionLiteral value, Array type)
  {
    final var itemType = type.getItemType();

    return switch (xsmpUtil.getPrimitiveTypeKind(itemType))
    {
      case BOOL -> boolArrayValue(value);
      case CHAR8 -> char8ArrayValue(value);
      case DATE_TIME -> dateTimeArrayValue(value);
      case DURATION -> durationArrayValue(value);
      case FLOAT32 -> float32ArrayValue(value);
      case FLOAT64 -> float64ArrayValue(value);
      case INT16 -> int16ArrayValue(value);
      case INT32 -> int32ArrayValue(value);
      case INT64 -> int64ArrayValue(value);
      case INT8 -> int8ArrayValue(value);
      case STRING8 -> string8ArrayValue(value);
      case UINT16 -> uint16ArrayValue(value);
      case UINT32 -> uint32ArrayValue(value);
      case UINT64 -> uint64ArrayValue(value);
      case UINT8 -> uint8ArrayValue(value);
      case ENUM -> enumArrayValue(value);
      case NONE -> arrayValue(value);
    };

  }

  private Value convert(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createStructureValue();

    for (final var elem : value.getElements())
    {

      final var result = convert(elem);
      if (result != null)
      {
        v.getFieldValue().add(result);
      }
    }
    updateField(v, value);
    return v;
  }

  private void updateField(Value value, Expression e)
  {
    if (e instanceof final DesignatedInitializer d && d.getField() != null)
    {
      value.setField(d.getField().getName());
    }
  }

  private Float32ArrayValue float32ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createFloat32ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(float32Value(i)));
    updateField(v, value);
    return v;
  }

  private Float64ArrayValue float64ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createFloat64ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(float64Value(i)));
    updateField(v, value);
    return v;
  }

  private DurationArrayValue durationArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createDurationArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(durationValue(i)));
    updateField(v, value);
    return v;
  }

  private DateTimeArrayValue dateTimeArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createDateTimeArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(dateTimeValue(i)));
    updateField(v, value);
    return v;
  }

  private Char8ArrayValue char8ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createChar8ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(char8Value(i)));
    updateField(v, value);
    return v;
  }

  private String8ArrayValue string8ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createString8ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(string8Value(i)));
    updateField(v, value);
    return v;
  }

  private BoolArrayValue boolArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createBoolArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(boolValue(i)));
    updateField(v, value);
    return v;
  }

  private BoolValue boolValue(Expression value)
  {
    final var v = TypesFactory.eINSTANCE.createBoolValue();
    v.setValue(xsmpUtil.getBoolean(value));
    updateField(v, value);
    return v;
  }

  private ArrayValue arrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(convert(i)));
    updateField(v, value);
    return v;
  }

  private Int8ArrayValue int8ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createInt8ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(int8Value(i)));
    updateField(v, value);
    return v;
  }

  private Int16ArrayValue int16ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createInt16ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(int16Value(i)));
    updateField(v, value);
    return v;
  }

  private Int32ArrayValue int32ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createInt32ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(int32Value(i)));
    updateField(v, value);
    return v;
  }

  private EnumerationArrayValue enumArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createEnumerationArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(enumValue(i)));
    updateField(v, value);
    return v;
  }

  private Int64ArrayValue int64ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createInt64ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(int64Value(i)));
    updateField(v, value);
    return v;
  }

  private UInt8ArrayValue uint8ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createUInt8ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(uint8Value(i)));
    updateField(v, value);
    return v;
  }

  private UInt16ArrayValue uint16ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createUInt16ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(uint16Value(i)));
    return v;
  }

  private UInt32ArrayValue uint32ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createUInt32ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(uint32Value(i)));
    updateField(v, value);
    return v;
  }

  private UInt64ArrayValue uint64ArrayValue(CollectionLiteral value)
  {
    final var v = TypesFactory.eINSTANCE.createUInt64ArrayValue();
    value.getElements().forEach(i -> v.getItemValue().add(uint64Value(i)));
    updateField(v, value);
    return v;
  }

  private DurationValue durationValue(final Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createDurationValue();

    final var duration = xsmpUtil.getDuration(expression);

    try
    {
      v.setValue(DatatypeFactory.newInstance().newDuration(duration / 1_000_000));
    }
    catch (final Exception e)
    {
      // ignore
    }

    updateField(v, expression);
    return v;
  }

  private DateTimeValue dateTimeValue(final Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createDateTimeValue();

    final var dateTime = xsmpUtil.getDateTime(expression);

    final var gc = new GregorianCalendar();
    gc.setTimeInMillis(dateTime / 1_000_000);
    gc.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
    try
    {
      v.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
    }
    catch (final Exception e)
    {
      // ignore
    }

    updateField(v, expression);
    return v;
  }

  private Char8Value char8Value(final Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createChar8Value();
    v.setValue(xsmpUtil.getChar8(expression));
    updateField(v, expression);
    return v;
  }

  private Int8Value int8Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createInt8Value();
    v.setValue(xsmpUtil.getInt8(expression));
    updateField(v, expression);
    return v;
  }

  private Int16Value int16Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createInt16Value();
    v.setValue(xsmpUtil.getInt16(expression));
    updateField(v, expression);
    return v;
  }

  private Int32Value int32Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createInt32Value();
    v.setValue(xsmpUtil.getInt32(expression));
    updateField(v, expression);
    return v;
  }

  private EnumerationValue enumValue(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createEnumerationValue();
    final var literal = xsmpUtil.getEnumerationLiteral(expression);
    v.setValue(xsmpUtil.getInt32(literal.getValue()));
    updateField(v, expression);
    return v;
  }

  private Int64Value int64Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createInt64Value();
    v.setValue(xsmpUtil.getInt64(expression));
    updateField(v, expression);
    return v;
  }

  private UInt8Value uint8Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createUInt8Value();
    v.setValue(xsmpUtil.getUInt8(expression));
    updateField(v, expression);
    return v;
  }

  private UInt16Value uint16Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createUInt16Value();
    v.setValue(xsmpUtil.getUInt16(expression));
    updateField(v, expression);
    return v;
  }

  private UInt32Value uint32Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createUInt32Value();
    v.setValue(xsmpUtil.getUInt32(expression));
    updateField(v, expression);
    return v;
  }

  private UInt64Value uint64Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createUInt64Value();
    v.setValue(xsmpUtil.getUInt64(expression));
    updateField(v, expression);
    return v;
  }

  private Float32Value float32Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createFloat32Value();
    v.setValue(xsmpUtil.getFloat32(expression));
    updateField(v, expression);
    return v;
  }

  private Float64Value float64Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createFloat64Value();
    v.setValue(xsmpUtil.getFloat64(expression));
    updateField(v, expression);
    return v;
  }

  private String8Value string8Value(Expression expression)
  {
    final var v = TypesFactory.eINSTANCE.createString8Value();
    v.setValue(xsmpUtil.getString8(expression));
    updateField(v, expression);
    return v;
  }

}