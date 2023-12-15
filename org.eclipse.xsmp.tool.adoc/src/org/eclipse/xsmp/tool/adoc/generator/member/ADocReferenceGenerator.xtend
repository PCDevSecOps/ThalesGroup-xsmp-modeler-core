package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xsmp.xcatalogue.Reference
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.xcatalogue.Component
import org.eclipse.xsmp.tool.adoc.ADocUtil
import com.google.inject.Inject

class ADocReferenceGenerator {
    
    @Inject extension ADocUtil

    def CharSequence generateContent(Component component, IFileSystemAccess2 fsa) {
        val references = component.member.filter(Reference)
        '''
            === References
            «IF references.empty»None.«ELSE»
            The model shall implement the reference parameters defined below.
             
            .Model Reference
            |===
            |Name |Comment |Type
            
            «FOR reference : references»
                «reference.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def protected CharSequence generate(Reference reference) {
        '''
            |«reference.name»
            |«reference.description»
            |«reference.type.fqn.toString("::")»
        '''
    }
}
