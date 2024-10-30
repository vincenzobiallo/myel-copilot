package com.luxottica.testautomation.constants;

public class Constants {

    public static final String COOKIE = "Cookie";
    public static final String COOKIE_SESSION_ID = "myl_frontoffice_sessionid";

    public static class ENDPOINTS {
        public static final String STORE_IDENTIFIER = "{storeIdentifier}";
        public static final String LOCALE = "{locale}";

        public static final String SESSION_URL = "/fo-bff/admin/current-session";
        public static final String PERSONIFICATION_URL = "/fo-bff/api/priv/v1/{storeIdentifier}/{locale}/personification/customers";
        public static final String PRECART = "/fo-bff/api/priv/v1/{storeIdentifier}/{locale}/precart";
        public static final String DOORS = "fo-bff/api/priv/v1/{storeIdentifier}/{locale}/doors";
        public static final String USER_CONTEXT = "/fo-bff/api/priv/v1/{storeIdentifier}/usercontext";
        public static final String FACETS = "/fo-bff/api/priv/v1/{storeIdentifier}/{locale}/search/byFacets/*";
    }

    public static class USER_ATTRIBUTES {
        public static final String URL = "url";
        public static final String STORE = "store";
        public static final String LOCALE = "locale";
        public static final String CURRENT_IMPERSONIFICATION = "currentImpersonification";
    }

    public static class KEYS {
        public static final String ENTER = "Enter";
    }
}
