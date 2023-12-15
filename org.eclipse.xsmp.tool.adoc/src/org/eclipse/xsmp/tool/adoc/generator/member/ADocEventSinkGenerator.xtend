package org.eclipse.xsmp.tool.adoc.generator.member

import org.eclipse.xsmp.xcatalogue.EventSink
import org.eclipse.xtext.generator.IFileSystemAccess2
import com.google.inject.Inject
import org.eclipse.xsmp.tool.adoc.ADocUtil
import org.eclipse.xsmp.xcatalogue.Component

class ADocEventSinkGenerator {

    @Inject extension ADocUtil

    def CharSequence generateContent(Component component, IFileSystemAccess2 fsa) {
        val eventSinks = component.member.filter(EventSink)
        '''
            === Event Sinks
            «IF eventSinks.empty»None.«ELSE»
            The model shall implement the events defined below.
             
            .Model Event Sink
            |===
            |Name |Comment
            
            «FOR eventSink : eventSinks»
                «eventSink.generate»
            «ENDFOR»
            |===
            «ENDIF»
        '''
    }

    def protected CharSequence generate(EventSink eventSink) {
        '''
            |«eventSink.name»
            |«eventSink.description»
            |«eventSink.type.fqn.toString("::")»
        '''
    }
}
