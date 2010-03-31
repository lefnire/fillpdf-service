package fillpdf

class FillpdfController {

   def index = { }

    // XML-RPC
    def fillpdfService
    def xmlrpc = {
        response.setContentType('text/xml')
        fillpdfService.service(request, response)
    }
}
