/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ocdevel;

import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;

/**
 *
 * @author renelle
 */

//# PDF::Stamper provides an interface into iText's PdfStamper allowing for the
//# editing of existing PDF's as templates. PDF::Stamper is not a PDF generator,
//# it allows you to edit existing PDF's and use them as templates.
//#
//# == Creation of templates
//#
//# Templates currently can only be created using Adobe LiveCycle
//# Designer which comes with the lastest versions of Adobe Acrobat
//# Professional.  Using LiveCycle Designer you can create a form and
//# add textfield's for text and button's for images.
//#
//# == Example
//#
//# pdf = PDF::Stamper.new("my_template.pdf")
//# pdf.text :first_name, "Jason"
//# pdf.text :last_name, "Yates"
//# pdf.image :photo, "photo.jpg"
//# pdf.save_as "my_output"
public class FillpdfService{
  ByteArrayOutputStream baos  = null;
  PdfStamper stamp            = null;
  AcroFields form             = null;


  public FillpdfService(String pdf, String type){
    if(type.equals("file")){
      template(pdf);
    }
  }

    public void template(String template){
      /*NOTE I'd rather use a ByteArrayOutputStream.  However I
        couldn't get it working.  Patches welcome. */
      //@tmp_path = File.join(Dir::tmpdir,  'pdf-stamper-' + rand(10000).to_s + '.pdf')
      PdfReader reader = null;
      try{
        reader = new PdfReader(template);
        this.baos = new ByteArrayOutputStream();
        this.stamp = new PdfStamper(reader, this.baos); //FileOutputStream.new(@tmp_path))
        this.form = this.stamp.getAcroFields();
      }catch(Exception e){ e.printStackTrace();}
      //@@TODO: Proper exception handling
      
}
//
//    # Set a button field defined by key and replaces with an image.
//    def image(key, image_path)
//      # Idea from here http://itext.ugent.be/library/question.php?id=31
//      # Thanks Bruno for letting me know about it.
//      img = Image.getInstance(image_path)
//      img_field = @form.getFieldPositions(key.to_s)
//
//      rect = Rectangle.new(img_field[1], img_field[2], img_field[3], img_field[4])
//      img.scaleToFit(rect.width, rect.height)
//      img.setAbsolutePosition(
//        img_field[1] + (rect.width - img.scaledWidth) / 2,
//        img_field[2] + (rect.height - img.scaledHeight) /2
//      )
//
//      cb = @stamp.getOverContent(img_field[0].to_i)
//      cb.addImage(img)
//    end
//
//    # Takes the PDF output and sends as a string.  Basically it's sole
//    # purpose is to be used with send_data in rails.
//    def to_s
//      fill
//      String.from_java_bytes(@baos.toByteArray)
//    end

//   # Set a textfield defined by key and text to value.
//    def text(key, value)
//      @form.setField(key.to_s, value.to_s) # Value must be a string or itext will error.
//    end
//
//    # Saves the PDF into a file defined by path given.
//    def save_as(file)
//      f = File.new(file, "w")
//      f.syswrite to_s
//    end
//
//    private
//
//    def fill
//      @stamp.setFormFlattening(true)
//      @stamp.close
//    end






//    def initialize(pdf = nil, options = {})
//      @treemap = Rjb::import('java.util.TreeMap')
//      @xfdfreader = Rjb::import('com.itextpdf.text.pdf.XfdfReader')
//
//      if(options['from']=='file')
//        super(pdf)
//      else
//        super() # make sure null-arg constructor
//        reader = @pdfreader.new_with_sig("[B", pdf)
//        @baos = @bytearray.new
//        @stamp = @pdfstamper.new(reader, @baos)
//        @form = @stamp.getAcroFields()
//      end
//    end
//
//    def parse
//      arr = []
//      fields = @form.getFields()
//      fs = @treemap.new(fields)
//      k = fs.keySet()  # @k gets sorted list of field names
//
//      it = k.iterator()
//      while it.hasNext()
//          # see http://www.ruby-forum.com/topic/187753
//          key = it.next().to_string
//
//          case @form.getFieldType(key)
//            when @acrofields.FIELD_TYPE_CHECKBOX:
//                type="Checkbox"
//            when @acrofields.FIELD_TYPE_COMBO:
//                type="Combobox"
//            when @acrofields.FIELD_TYPE_LIST:
//                type="List"
//            when @acrofields.FIELD_TYPE_NONE:
//                type="None"
//            when @acrofields.FIELD_TYPE_PUSHBUTTON:
//                type="Pushbutton"
//            when @acrofields.FIELD_TYPE_RADIOBUTTON:
//                type="Radiobutton"
//            when @acrofields.FIELD_TYPE_SIGNATURE:
//                type="Signature"
//            when @acrofields.FIELD_TYPE_TEXT:
//                type="Text"
//            else
//                type="?"
//          end
//          arr << {'name'=>key, 'type'=>type, 'value'=>form.getField(key)}
//      end
//      arr
//    end
//
//    def parse_as_xfdf
//      xml = "<fields>"
//      arr = parse()
//      arr.each do |field|
//#        xml += "\n<field name='#{key}' type='#{form.getFieldType(key)}' value='#{form.getField(key)}'/>"
//        xml += "\n<field name='#{field['name']}' type='#{field['type']}' value='#{field['value']}'/>"
//      end
//      xml += "</fields>"
//    end
//
//    def merge(xfdf)
//      xfdfreader = @xfdfreader.new_with_sig("[B", xfdf)
//      @form.setFields(xfdfreader)
//    end
//
//    # Set a button field defined by key and replaces with an image.
//    def image_bytes(key, image_bytes)
//      # Idea from here http://itext.ugent.be/library/question.php?id=31
//      # Thanks Bruno for letting me know about it.
//      image = Rjb::import('com.itextpdf.text.Image')
//  #      img = image.getInstance(image_path)
//      img = image._invoke('getInstance', '[B', image_bytes)
//      img_field = @form.getFieldPositions(key.to_s)
//
//      rectangle = Rjb::import('com.itextpdf.text.Rectangle')
//      rect = rectangle.new(img_field[1], img_field[2], img_field[3], img_field[4])
//      img.scaleToFit(rect.width, rect.height)
//      img.setAbsolutePosition(
//        img_field[1] + (rect.width - img.getScaledWidth) / 2,
//        img_field[2] + (rect.height - img.getScaledHeight) /2
//      )
//      cb = @stamp.getOverContent(img_field[0].to_i)
//      cb.addImage(img)
//    end

}