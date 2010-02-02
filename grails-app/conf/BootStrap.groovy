class BootStrap {

     def init = { servletContext ->
         new FillpdfLicense(maxUses:5, licenseKey:"test", numUses:0).save()
     }
     def destroy = {
     }
} 