package org.eclipse.xsmp.tool.adoc.generator.type

import org.eclipse.xsmp.xcatalogue.Structure
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xsmp.tool.adoc.ADocUtil
import com.google.inject.Inject
import org.eclipse.xsmp.xcatalogue.Namespace
import org.eclipse.xsmp.xcatalogue.Field
import org.eclipse.xsmp.tool.adoc.generator.member.ADocFieldGenerator

class ADocStructureGenerator {
    
    @Inject extension ADocUtil
    
    @Inject
    ADocFieldGenerator fieldGenerator

    def CharSequence generateContent(Namespace namespace, IFileSystemAccess2 fsa) {
        val structures = namespace.member.filter(Structure)
        '''
            == Structures
            «IF structures.empty»None.«ELSE»
            
            «FOR structure : structures SEPARATOR '\n'»
                «structure.generate(fsa)»
            «ENDFOR»
            
            «ENDIF»
        '''
    }
    
    def protected CharSequence generate(Structure structure, IFileSystemAccess2 fsa) {
        var fields = structure.member.filter(Field)
        '''
            .«structure.fqn.toString("::")»
            |===
            |Name |Comment |Type |Unit |Initial Value
            
            «FOR field : fields»
                «fieldGenerator.generate(field)»
            «ENDFOR»
            |===
        '''
    }
}
