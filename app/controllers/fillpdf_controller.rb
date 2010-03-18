require 'xmlrpc/server'
require 'vendor/plugins/pdf-stamper/lib/pdf/fillpdf.rb'
require 'base64'

# XMLRPC controller, based on http://nathan.crause.name/entries/programming/xlm-rpc-under-ruby-on-rails
# see also, http://stackoverflow.com/questions/1967194/is-there-an-example-of-using-activeresource-and-xmlrpc-for-rails
class FillpdfController < ApplicationController
  
  # XML-RPC calls are not session-aware, so always turn this off
  session :off

  def initialize
    @server = XMLRPC::BasicServer.new
    # loop through all the methods, adding them as handlers
    self.class.instance_methods(false).each do |method|
      unless ['index'].member?(method)
        @server.add_handler(method) do |*args|
          self.send(method.to_sym, *args)
        end
      end
    end
  end

  def index
    result = @server.process(request.body)
    puts "\n\n----- BEGIN RESULT -----\n#{result}\n----- END RESULT -----\n"
    render :text => result, :content_type => 'text/xml'
  end
  
#     This function takes a data string representing the PDF document and parses its Acro
#     	Form Fields, returning its results as an XML documents.
#      @param pdf_data The PDF is Byte64-encoded on the client and sent as a string to
#      		this function (not a byte[], that's not working for some reason.)
#      @return An XML document is returned as a Byte64-encoded string; therefore, the client
#      		must decode the string to read the document and parse the elements (which represent
#      		fields in the original PDF document).
  def parse_pdf(pdf, auth_key)
    parsed = PDF::Fillpdf.new(Base64.decode64(pdf), {:from=>'bytes'})
    xml = parsed.parse_as_xfdf
    #TODO: don't need to encode it, return as struct
    return {'data' => Base64.encode64(xml)}
  end


#      This function merges a PDF to an XFDF.  Both are passed in as Byte64 Strings (instead of
#      	byte[] arrays) and the merged & flattened PDF is returned as a Byte64 String.
#      @param pdf_data The PDF is Byte64-encoded on the client and sent as a string to
#          		this function (not a byte[], that's not working for some reason.)
#      @param xfdf_data same as pdf_data, but for the XFDF
#      @return A merged & flattened PDF w XFDF in Byte64 String format.  (You'll need to
#      	Byte64-decode on the client)
  def merge_pdf(pdf, fields, auth_key)
    pdf = PDF::Fillpdf.new(Base64.decode64(pdf))
    fields.each do |key, value|
      #previous technique on image-detection, see r105
#      mutable_key = String.new(key.to_s) # some stupid "can't modify frozen string" exception, http://www.ruby-forum.com/topic/122253#544890
#      field_type = mutable_key.slice!(0,1) # i for image, t for text.  maybe not the best way to do this.
      if(value.slice(0,7) == "{image}")
        value.slice!(0,7)
        pdf.image(key, Base64.decode64(value))
      else
        pdf.text(key, value)
      end
    end
    #TODO: add image support
      #TODO: pass in fields as array, rather than xfdf
#      send_data(fileinbytes, {:filename => "Letters For Printing.pdf", :type => "application/pdf"})
    return {'data' => Base64.encode64(pdf.to_s) }
  end
end
