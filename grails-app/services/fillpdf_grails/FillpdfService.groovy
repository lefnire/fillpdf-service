package fillpdf_grails

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfdfReader;
import com.itextpdf.text.pdf.codec.Base64;

class FillpdfService extends Xmlrpc{
    boolean transactional = false

//    def serviceMethod() {
//
//    }

    static final def mapping = [ 'parse_pdf' : 'parsePdf', 'merge_pdf' : 'mergePdf' ]

//    private boolean verifyLicense(licenseKey) {
//        def license = FillpdfLicense.findByLicenseKey(Base64.decode(licenseKey))
//        if(lincense.maxUses<=license.numUses)
//        if(!license)
//            return false
//        license.numUses++
//        license.save()
//        return true
//    }
    /**
     * This function takes a data string representing the PDF document and parses its Acro
     * 	Form Fields, returning its results as an XML documents.
     * @param pdf_data The PDF is Byte64-encoded on the client and sent as a string to
     * 		this function (not a byte[], that's not working for some reason.)
     * @return An XML document is returned as a Byte64-encoded string; therefore, the client
     * 		must decode the string to read the document and parse the elements (which represent
     * 		fields in the original PDF document).
     */
    def parsePdf(params){
//        if(!verifyLicense(params[1]))
//            return "auth_error"

        String pdf_data = params[0]
        String xml=new String("<fields>")

         try{
                PdfReader reader = new PdfReader(Base64.decode(pdf_data));
                AcroFields form = reader.getAcroFields();
                HashMap fields = form.getFields();
                String key, type;
                int i=0;
                for (Iterator it = fields.keySet().iterator(); it.hasNext(); ) {
                        key = (String) it.next();
                        xml += "\n<field name=\""+key+"\" ";
                        switch(form.getFieldType(key)) {
                                case AcroFields.FIELD_TYPE_CHECKBOX:
                                        type="Checkbox";
                                        break;
                                case AcroFields.FIELD_TYPE_COMBO:
                                        type="Combobox";
                                        break;
                                case AcroFields.FIELD_TYPE_LIST:
                                        type="List";
                                        break;
                                case AcroFields.FIELD_TYPE_NONE:
                                        type="None";
                                        break;
                                case AcroFields.FIELD_TYPE_PUSHBUTTON:
                                        type="Pushbutton";
                                        break;
                                case AcroFields.FIELD_TYPE_RADIOBUTTON:
                                        type="Radiobutton";
                                        break;
                                case AcroFields.FIELD_TYPE_SIGNATURE:
                                        type="Signature";
                                        break;
                                case AcroFields.FIELD_TYPE_TEXT:
                                        type="Text";
                                        break;
                                default:
                                        type="?";
                        }
                        //xml += "type=\""+type+"\" value=\""+form.getField(key)+"\"/>";
                        xml += "type=\""+form.getFieldType(key)+"\" value=\""+form.getField(key)+"\"/>";
                }
                xml += "</fields>";
          }
          catch(Exception e){
                 e.printStackTrace();
          }

          return Base64.encodeBytes(xml.getBytes());
        }

    /**
     * This function merges a PDF to an XFDF.  Both are passed in as Byte64 Strings (instead of
     * 	byte[] arrays) and the merged & flattened PDF is returned as a Byte64 String.
     * @param pdf_data The PDF is Byte64-encoded on the client and sent as a string to
         * 		this function (not a byte[], that's not working for some reason.)
     * @param xfdf_data same as pdf_data, but for the XFDF
     * @return A merged & flattened PDF w/ XFDF in Byte64 String format.  (You'll need to
     * 	Byte64-decode on the client)
     */
    def mergePdf(params){
//        if(!verifyLicense(params[2]))
//            return "auth_error"

        String pdf_data = params[0]
        String xfdf_data = params[1]
           try{
                byte[] xfdf_bytes=Base64.decode(xfdf_data);
                byte[] pdf_bytes=Base64.decode(pdf_data);

                ByteArrayOutputStream baos = new ByteArrayOutputStream(); // FileOutputStream("out.pdf")
                PdfReader pdfreader = new PdfReader(pdf_bytes);
                PdfStamper stamp = new PdfStamper(pdfreader, baos);
                XfdfReader xfdfreader = new XfdfReader(xfdf_bytes);
                AcroFields form = stamp.getAcroFields();
                form.setFields(xfdfreader);
                stamp.setFormFlattening(true);
                stamp.close();

                return Base64.encodeBytes(baos.toByteArray());

                }catch(Exception e){
                        e.printStackTrace();
//             			response.setContentType("text/html");
//             			PrintWriter out_writer = response.getWriter();
//             			e.printStackTrace(out_writer);
                        return new String("error");
                }
        }
}
