package org.eclipse.xsmp.tool.adoc.generator.type

import com.google.inject.Inject
import org.eclipse.xsmp.tool.adoc.generator.member.ADocEntryPointGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocEventSinkGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocEventSourceGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocOperatorGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocReferenceGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.tool.adoc.ADocUtil
import org.eclipse.xsmp.xcatalogue.Namespace
import org.eclipse.xsmp.xcatalogue.Component
import org.eclipse.xsmp.tool.adoc.generator.member.ADocOutputGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocInputGenerator
import org.eclipse.xsmp.tool.adoc.generator.member.ADocFieldGenerator

class ADocComponentGenerator {

    @Inject extension ADocUtil
    
    @Inject
    ADocInputGenerator inputGenerator;
    
    @Inject
    ADocOutputGenerator outputGenerator;

    @Inject
    ADocEventSourceGenerator eventSourceGenerator;

    @Inject
    ADocEventSinkGenerator eventSinkGenerator;

    @Inject
    ADocEntryPointGenerator entryPointGenerator;

    @Inject
    ADocReferenceGenerator referenceGenerator;

    @Inject
    ADocOperatorGenerator operatorGenerator;

    @Inject
    ADocFieldGenerator fieldGenerator;

    def CharSequence generateContent(Namespace namespace, IFileSystemAccess2 fsa) {
        '''
            «FOR component : namespace.member.filter(Component)»
                «component.generate(fsa)»
            «ENDFOR»
        '''
    }

    def protected CharSequence generate(Component component, IFileSystemAccess2 fsa) {
        '''
            == «component.fqn.toString("::")»
            
            «inputGenerator.generateContent(component, fsa)»
            
            «outputGenerator.generateContent(component, fsa)»
            
            «eventSourceGenerator.generateContent(component, fsa)»
            
            «eventSinkGenerator.generateContent(component, fsa)»
            
            «entryPointGenerator.generateContent(component, fsa)»
            
            «referenceGenerator.generateContent(component, fsa)»
            
            «operatorGenerator.generateContent(component, fsa)»
            
            «fieldGenerator.generateContent(component, fsa)»
        '''
    }
}
