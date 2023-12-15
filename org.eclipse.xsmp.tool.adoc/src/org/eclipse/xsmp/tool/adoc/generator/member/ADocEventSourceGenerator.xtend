package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xtext.generator.IFileSystemAccess2
import com.google.inject.Inject
import org.eclipse.xsmp.tool.adoc.ADocUtil
import org.eclipse.xsmp.xcatalogue.Component
import org.eclipse.xsmp.xcatalogue.EventSource

class ADocEventSourceGenerator {

    @Inject extension ADocUtil

    def CharSequence generateContent(Component component, IFileSystemAccess2 fsa) {
        val eventSources = component.member.filter(EventSource)
        '''
            === Event Source
            «IF eventSources.empty»None.«ELSE»
            The model shall implement the events defined below.
             
            .Model Event Source
            |===
            |Name |Comment
            
            «FOR eventSource : eventSources»
                «eventSource.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def protected CharSequence generate(EventSource eventSource) {
        '''
            |«eventSource.name»
            |«eventSource.description»
            |«eventSource.type.fqn.toString("::")»
        '''
    }
}
