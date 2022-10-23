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
package org.eclipse.xsmp.profile.xsmp.generator.cpp.type

import com.google.inject.Inject
import org.eclipse.xsmp.generator.cpp.IncludeAcceptor
import org.eclipse.xsmp.generator.cpp.type.ComponentGenerator
import org.eclipse.xsmp.profile.xsmp.generator.cpp.FieldHelper
import org.eclipse.xsmp.xcatalogue.Component
import org.eclipse.xsmp.xcatalogue.Container
import org.eclipse.xsmp.xcatalogue.EntryPoint
import org.eclipse.xsmp.xcatalogue.EventSink
import org.eclipse.xsmp.xcatalogue.EventSource
import org.eclipse.xsmp.xcatalogue.Field
import org.eclipse.xsmp.xcatalogue.Model
import org.eclipse.xsmp.xcatalogue.NamedElement
import org.eclipse.xsmp.xcatalogue.NamedElementWithMembers
import org.eclipse.xsmp.xcatalogue.Operation
import org.eclipse.xsmp.xcatalogue.Parameter
import org.eclipse.xsmp.xcatalogue.Reference
import org.eclipse.xsmp.xcatalogue.SimpleType

class XsmpComponentGenerator extends ComponentGenerator {

	@Inject extension FieldHelper


	override collectIncludes(Component type, IncludeAcceptor acceptor) {
		super.collectIncludes(type, acceptor)
		acceptor.mdkHeader("Xsmp/" + type.eClass.name + ".h")
		if (type.useDynamicInvocation) {
			acceptor.systemHeader("map")
			acceptor.mdkSource("Smp/IPublication.h")
			acceptor.mdkSource("Xsmp/Request.h")
		}
	}

	override protected declareGen(Component type, Field f, boolean useGenPattern) {

		if (f.isMdkField)
			'''
				«f.comment»
				«IF f.isMutable»mutable «ENDIF»::Xsmp::Field<::«f.type.fqn.toString("::")»>«IF f.transient»::transient«ENDIF»«IF f.input»::input«ENDIF»«IF f.output»::output«ENDIF»«IF f.failure»::failure«ENDIF»«IF f.forcible»::forcible«ENDIF» «f.name»;
			'''
		else
			super.declareGen(type, f, useGenPattern)
	}

	override protected base(Component e) {
		var base = '''::Xsmp::«e.eClass.name»<«IF e.base !==null»::«e.base.fqn.toString("::")»«ENDIF»>'''

		if (e.member.exists[it instanceof Container])
			base += '''::WithContainers'''
		if (e.member.exists[it instanceof Reference])
			base += '''::WithReferences'''
		if (e.useDynamicInvocation)
			base += '''::WithOperations'''
		if (e.member.exists[it instanceof EventSource])
			base += '''::WithEventSources'''
		if (e.member.exists[it instanceof EventSink])
			base += '''::WithEventSinks'''
		if (e.member.exists[it instanceof EntryPoint])
			base += '''::WithEntryPoints'''
		if (e instanceof Model && e.member.filter(Field).exists[it.isFailureField])
			base += '''::WithFailures'''

		return base
	}

	protected override CharSequence generateRqHandlerParam(NamedElementWithMembers container, Operation o,
		boolean useGenPattern) {

		val r = o.returnParameter

		'''
			if (handlers.find("«o.name»") == handlers.end()) {
				handlers["«o.name»"] = [](«container.name(useGenPattern)»* cmp, ::Smp::IRequest* req) {
				«FOR p : o.parameter»
					«p.initParameter(container)»
				«ENDFOR»
				
				/// Invoke «o.name»
				«IF r !== null»auto p_«r.name» = «ENDIF»cmp->«o.name»(«FOR p : o.parameter SEPARATOR ', '»«IF p.isByPointer»&«ENDIF»p_«p.name»«ENDFOR»);
				
				«FOR p : o.parameter»
				«p.setParameter(container)»
				«ENDFOR»
				«IF r !== null»::Xsmp::Request::setReturnValue(req, «r.type.generatePrimitiveKind», p_«r.name»);«ENDIF»
				};
			}
		'''
	}

	override CharSequence initParameter(Parameter p, NamedElement context) {
		switch (p.direction) {
			case IN,
			case INOUT: {
				// declare and initialize the parameter
				if (p.type instanceof SimpleType)
					'''
						auto p_«p.name» = ::Xsmp::Request::get<::«p.type.fqn.toString("::")»>(cmp, req, "«p.name»", «p.type.generatePrimitiveKind»«IF p.^default !== null», «p.^default.generateExpression(p.type, context)»«ENDIF»);
					'''
				else
					'''
						auto p_«p.name» = ::Xsmp::Request::get<::«p.type.fqn.toString("::")»>(cmp, req, "«p.name»", «p.type.uuidQfn»«IF p.^default !== null», «p.^default.generateExpression(p.type, context)»«ENDIF»);
					'''
			}
			default: {
				// only declare the parameter
				'''
					::«p.type.fqn.toString("::")» p_«p.name» «p.^default?.generateExpression(p.type, context)»;
				'''
			}
		}
	}

	override CharSequence setParameter(Parameter p, NamedElement context) {

		switch (p.direction) {
			case OUT,
			case INOUT: {
				'''
					::Xsmp::Request::set(cmp, req, "«p.name»", «p.type.generatePrimitiveKind», p_«p.name»);
				'''
			}
			default: {
				// do nothing
			}
		}
	}

}
