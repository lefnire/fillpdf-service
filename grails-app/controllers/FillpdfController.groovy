class FillpdfController {

//    def index = { }
    def fillpdfService

    def test = {
        render "test"
    }

    def xmlrpc = {
        response.setContentType('text/xml')
        fillpdfService.service(request, response)
    }
}
