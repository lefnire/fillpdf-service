class FillpdfLicense {
    Integer maxUses //enum type {FREE, PLUS, PREMIUM}
    Integer numUses
    String licenseKey
//    Date startDate, endDate

//    static mapping = {
//        id column:'key'
//    }
    static constraints = {
//        licenseMax(inList:[100, 1000])
    }
}