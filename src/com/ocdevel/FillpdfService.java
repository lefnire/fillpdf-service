package com.ocdevel;

import java.io.IOException;
import com.itextpdf.text.DocumentException;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.XfdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.codec.Base64;

/**
 *
 * @author renelle
 */

/**
 *  PDF::Stamper provides an interface into iText's PdfStamper allowing for the
 *  editing of existing PDF's as templates. PDF::Stamper is not a PDF generator,
 *  it allows you to edit existing PDF's and use them as templates.
 *
 *  == Creation of templates
 *
 *  Templates currently can only be created using Adobe LiveCycle
 *  Designer which comes with the lastest versions of Adobe Acrobat
 *  Professional.  Using LiveCycle Designer you can create a form and
 *  add textfield's for text and button's for images.
 *
 *  == Example
 *
 *  pdf = PDF::Stamper.new("my_template.pdf")
 *  pdf.text :first_name, "Jason"
 *  pdf.text :last_name, "Yates"
 *  pdf.image :photo, "photo.jpg"
 *  pdf.save_as "my_output"
 */
public class FillpdfService{
  ByteArrayOutputStream baos  = null;
  PdfStamper stamp            = null;
  AcroFields form             = null;

  public static void main(String [ ] args){

    FillpdfService svc = new FillpdfService("/Users/lefnire/test_template.pdf", "file");
    //svc.text("Date of Birth", "test");
    svc.text("form1[0].#subform[0].text_field01[0]", "test");
//    System.out.println(svc.toString());
    svc.saveAs("/Users/lefnire/Downloads/test.pdf");
  }



  public FillpdfService(String pdf, String type){
    PdfReader reader = null;

    /*NOTE I'd rather use a ByteArrayOutputStream.  However I
      couldn't get it working.  Patches welcome. */
    //@tmp_path = File.join(Dir::tmpdir,  'pdf-stamper-' + rand(10000).to_s + '.pdf')
    try{
      if(type.equals("file")){
        reader = new PdfReader(pdf);
      }else{
        reader = new PdfReader(pdf.getBytes());
      }
      this.baos = new ByteArrayOutputStream();
      this.stamp = new PdfStamper(reader, this.baos); //FileOutputStream.new(@tmp_path))
      this.form = this.stamp.getAcroFields();
    }
    catch(IOException e){ e.printStackTrace();}
    catch(DocumentException d){ d.printStackTrace();}
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
    /**
     * Takes the PDF output and sends as a string.  Basically it's sole
     * purpose is to be used with send_data in rails.
     */
    @Override
    public String toString(){
      this.fill();
      return Base64.encodeBytes(baos.toByteArray());     
    }

    /**
     * Set a textfield defined by key and text to value.
     */
    public void text(String key, String value){
      try{
        this.form.setField(key, value); // Value must be a string or itext will error.
      }
      catch(IOException e){e.printStackTrace();}
      catch(DocumentException d){d.printStackTrace();}
    }

    // Saves the PDF into a file defined by path given.
    public void saveAs(String file){
      try{
        this.fill();
        FileOutputStream fout = new FileOutputStream(file);
        this.baos.writeTo(fout);
        fout.close();
      }
      catch(IOException e){e.printStackTrace();}

    }

    private void fill(){
      try{
        this.stamp.setFormFlattening(true);
        this.stamp.close();
        this.baos.flush();
      }
      catch(IOException e){e.printStackTrace();}
      catch(DocumentException d){d.printStackTrace();}

    }





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

    public void merge(String xfdf, String type)
      throws IOException, DocumentException {
      XfdfReader xfdfreader = null;
      if(type.equals("file")){
        xfdfreader = new XfdfReader(xfdf);
      }else{
        xfdfreader = new XfdfReader(xfdf.getBytes());
      }
      this.form.setFields(xfdfreader);
    }
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