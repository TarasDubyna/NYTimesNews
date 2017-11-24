package taras.nytimesnews.Network;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class NetworkConnection {

    // url example https://api.nytimes.com/svc/mostpopular/v2/   mostemailed/Arts/1.json?api-key=2586ad6dfc1e4956bf10a11517bd878d
    public final static String NYTIMES_URL = "https://api.nytimes.com/svc/mostpopular/v2/";
    public final static String MY_API_KEY = "2586ad6dfc1e4956bf10a11517bd878d";

    public final static String MOST_MAILED = "mostemailed/";
    public final static String MOST_SHARED = "mostshared/";
    public final static String MOST_VIEWED = "mostviewed/";

    public final static String SECTION = "section";
    public final static String TIME_PERIOD = "time-period";

    private String request_url = null;

    private String mostParam = MOST_MAILED;
    private String section = "Arts";
    private int timePeriod = 1;


    NetworkConnection networkConnection;

    public NetworkConnection() {
        networkConnection = new NetworkConnection();
    }

    private NetworkConnection(BuildRequestParam builder){
        section = builder.section;
        timePeriod = builder.timePeriod;
        mostParam = builder.mostParam;
        request_url = NYTIMES_URL + mostParam + section + "/" + timePeriod + ".json";
    }
    public static class BuildRequestParam{
        private String mostParam = null;
        private String section = null;
        private int timePeriod = 0;

        public BuildRequestParam mostParam(String value){
            mostParam = value;
            return this;
        }

        public BuildRequestParam section (String value){
            section = value;
            return this;
        }

        public BuildRequestParam timePeriod (int value){
            timePeriod = value;
            return this;
        }

        public NetworkConnection build(){
            return new NetworkConnection(this);
        }

    }


    public RequestParams getRequestApiKey(){
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", MY_API_KEY);
        return requestParams;
    }
    public String getRequestUrl(){
        return request_url;
    }



}
