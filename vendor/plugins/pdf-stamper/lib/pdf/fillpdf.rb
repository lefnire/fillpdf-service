require 'pdf/stamper.rb'
module PDF
  class Fillpdf < Stamper
    attr_reader :form #don't know why this is necessary... is it something with protected fields & inheritance?
    
    def initialize(pdf = nil, options = {})
      @treemap = Rjb::import('java.util.TreeMap')
      @xfdfreader = Rjb::import('com.itextpdf.text.pdf.XfdfReader')
      
      if(options['from']=='file')
        super(pdf)
      else
        super() # make sure null-arg constructor
        reader = @pdfreader.new_with_sig("[B", pdf)
        @baos = @bytearray.new
        @stamp = @pdfstamper.new(reader, @baos)
        @form = @stamp.getAcroFields()
      end
    end
    
    def parse
      arr = []
      fields = @form.getFields()
      fs = @treemap.new(fields)
      k = fs.keySet()  # @k gets sorted list of field names
  
      it = k.iterator()
      while it.hasNext()
          # see http://www.ruby-forum.com/topic/187753
          key = it.next().to_string
  
          case @form.getFieldType(key)
            when @acrofields.FIELD_TYPE_CHECKBOX:
                type="Checkbox"
            when @acrofields.FIELD_TYPE_COMBO:
                type="Combobox"
            when @acrofields.FIELD_TYPE_LIST:
                type="List"
            when @acrofields.FIELD_TYPE_NONE:
                type="None"
            when @acrofields.FIELD_TYPE_PUSHBUTTON:
                type="Pushbutton"
            when @acrofields.FIELD_TYPE_RADIOBUTTON:
                type="Radiobutton"
            when @acrofields.FIELD_TYPE_SIGNATURE:
                type="Signature"
            when @acrofields.FIELD_TYPE_TEXT:
                type="Text"
            else
                type="?"
          end
          arr << {'name'=>key, 'type'=>type, 'value'=>form.getField(key)}
      end
      arr
    end
    
    def parse_as_xfdf
      xml = "<fields>"
      arr = parse()
      arr.each do |field|
#        xml += "\n<field name='#{key}' type='#{form.getFieldType(key)}' value='#{form.getField(key)}'/>"
        xml += "\n<field name='#{field['name']}' type='#{field['type']}' value='#{field['value']}'/>"
      end
      xml += "</fields>"
    end
  
    def merge(xfdf)
      xfdfreader = @xfdfreader.new_with_sig("[B", xfdf)
      @form.setFields(xfdfreader)
    end
  
    # Set a button field defined by key and replaces with an image.
    def image_bytes(key, image_bytes)
      # Idea from here http://itext.ugent.be/library/question.php?id=31 
      # Thanks Bruno for letting me know about it.
      image = Rjb::import('com.itextpdf.text.Image')
  #      img = image.getInstance(image_path)
      img = image._invoke('getInstance', '[B', image_bytes)
      img_field = @form.getFieldPositions(key.to_s)
  
      rectangle = Rjb::import('com.itextpdf.text.Rectangle')
      rect = rectangle.new(img_field[1], img_field[2], img_field[3], img_field[4])
      img.scaleToFit(rect.width, rect.height)
      img.setAbsolutePosition(
        img_field[1] + (rect.width - img.getScaledWidth) / 2,
        img_field[2] + (rect.height - img.getScaledHeight) /2
      )
      cb = @stamp.getOverContent(img_field[0].to_i)
      cb.addImage(img)
    end
  
 end 
end