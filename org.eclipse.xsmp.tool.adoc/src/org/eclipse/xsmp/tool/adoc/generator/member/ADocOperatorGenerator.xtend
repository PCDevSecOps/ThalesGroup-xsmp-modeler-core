package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xsmp.xcatalogue.Operation
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.xcatalogue.Component

class ADocOperatorGenerator {

    def CharSequence generateContent(Component component, IFileSystemAccess2 fsa) {
        val operations = component.member.filter(Operation)
        '''
            === Operations
            «IF operations.empty»None.«ELSE»
            The model shall implement the operations defined below.
             
            .Entry Points
            |===
            |Name |Parameter |Comment
            
            «FOR operation : operations»
                «operation.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def protected CharSequence generate(Operation operation) {
        '''
            |«operation.name»
            |TODO PARAMETERS
            |«operation.description»
        '''
    }
}
