package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.files.File;
import javafx.scene.control.Button;

/**
 * Created by Alessa on 03.07.16.
 */
public class TabButton extends Button {

   private File code;
   private File test;

   public TabButton(String text, File code, File test){
      super(text);
      this.code = code;
      this.test = test;
   }

   public File getCode() {
      return code;
   }

   public File getTest() {
      return test;
   }
}
