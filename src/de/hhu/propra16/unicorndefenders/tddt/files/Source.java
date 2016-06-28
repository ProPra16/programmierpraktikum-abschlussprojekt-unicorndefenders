package de.hhu.propra16.unicorndefenders.tddt.files;

import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerResult;

import java.util.Collection;

/**
 * Quellcode-Datei
 *
 * @author Pascal
 */
public class Source extends File {

   /**
    * Liste von Compilerfehlern
    */
   protected Collection<CompileError> compilerErrors;

   /**
    * Uebersetzungseinheit fuer die Quellcode-Datei
    */
   protected CompilationUnit compilationUnit;

   /**
    * Konstruktor.
    *
    * @param file
    */
   public Source(File file) {
      content = file.content;
      logicalName = file.logicalName;

      compilationUnit = new CompilationUnit(logicalName, content, false);
   }

   /**
    * Liste der Kompilierungsfehler setzen.
    *
    * @param result
    */
   public void setCompilerErrors(CompilerResult result) {
      compilerErrors = result.getCompilerErrorsForCompilationUnit(compilationUnit);
   }

   public Collection<CompileError> getCompilerErrors() {
      return compilerErrors;
   }

   CompilationUnit getCompilationUnit() {
      return compilationUnit;
   }
}
