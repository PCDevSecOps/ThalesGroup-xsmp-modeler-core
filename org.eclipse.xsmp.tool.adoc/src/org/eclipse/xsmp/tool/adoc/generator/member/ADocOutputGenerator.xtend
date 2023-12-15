package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xsmp.xcatalogue.Field
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.tool.adoc.ADocUtil
import com.google.inject.Inject
import org.eclipse.xsmp.xcatalogue.NamedElementWithMembers

class ADocOutputGenerator {
    
    @Inject extension ADocUtil

    def CharSequence generateContent(NamedElementWithMembers elem, IFileSystemAccess2 fsa) {
        val fields = elem.member.filter(Field).filter[it | it.isOutput]
        '''
            === Outputs
            «IF fields.empty»None.«ELSE»
            The model shall implement the outputs parameters defined below.
             
            .Model outputs
            |===
            |Name |Comment |Type |Unit
            
            «FOR field : fields»
                «field.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def CharSequence generate(Field field) {
        '''
            |«field.name»
            |«field.description»
            |«field.type.fqn.toString("::")»
            |N.A.
        '''
    }
}
