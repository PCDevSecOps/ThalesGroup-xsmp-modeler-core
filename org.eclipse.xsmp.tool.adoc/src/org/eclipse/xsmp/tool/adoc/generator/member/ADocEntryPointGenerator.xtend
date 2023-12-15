package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xsmp.xcatalogue.EntryPoint
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.xcatalogue.Component

class ADocEntryPointGenerator {

    def CharSequence generateContent(Component component, IFileSystemAccess2 fsa) {
        val entryPoints = component.member.filter(EntryPoint)
        '''
            === Entry Points
            «IF entryPoints.empty»None.«ELSE»
            The model shall implement the entry points defined below.
             
            .Entry Points
            |===
            |Name |Comment
            
            «FOR entryPoint : entryPoints»
                «entryPoint.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def protected CharSequence generate(EntryPoint entryPoint) {
        '''
            |«entryPoint.name»
            |«entryPoint.description»
        '''
    }
}
