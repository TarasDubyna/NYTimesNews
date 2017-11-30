package taras.nytimesnews.Network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.MediaParam;

public class NetworkConnection extends AsyncHttpClient{

    // url example https://api.nytimes.com/svc/  mostpopular/v2/   mostemailed/ Arts/ 1.json?api-key=2586ad6dfc1e4956bf10a11517bd878d
    public final static String NYTIMES_URL = "https://api.nytimes.com/svc/";

    public final static String API_KEY = "2586ad6dfc1e4956bf10a11517bd878d";

    public final static String TYPE_REQUEST = "type_request";
    public final static String MOST_POPULAR_TYPE = "most_popular_type";

    public final static String MOST_MAILED = "mostemailed/";
    public final static String MOST_SHARED = "mostshared/";
    public final static String MOST_VIEWED = "mostviewed/";

    public final static String MOST_POPULAR_REQUEST = "mostpopular/v2/";
    public final static String SEARCH_REQUEST = "";
    public final static String ARCHIVE_REQUEST = "archive/v1/";

    public final static String SECTION = "section";
    public final static String TIME_PERIOD = "time-period";

    private String request_url = null;


    private String typeRequest = "mostpopular/v2/";

    private String mostPopularType = MOST_MAILED;
    private String section = "Arts";
    private int timePeriod = 1;

    private int year = 0;
    private int month = 0;

    private NetworkConnection(BuildRequestParam builder){
        request_url = builder.request_url;
    }


    public RequestHandle createRequest(ResponseHandlerInterface responseHandler) {
        return super.get(request_url, getRequestApiKey(), responseHandler);
    }

    public static class BuildRequestParam{
        private String request_url = null;

        private String typeRequest = null;

        //mostpopular param
        private String mostPopularType = null;
        private String section = null;
        private int timePeriod = 0;

        //archive param
        private int year = 0;
        private int month = 0;

        public BuildRequestParam typeRequest(String value){
            typeRequest = value;
            return this;
        }
        public BuildRequestParam mostPopularParams(String mostPopularType, String section, int timePeriod){
            this.typeRequest = MOST_POPULAR_REQUEST;
            this.mostPopularType = mostPopularType;
            this.section = section;
            this.timePeriod = timePeriod;
            return this;
        }
        public BuildRequestParam archiveParams(int year, int month){
            this.year = year;
            this.month = month;
            return this;
        }
        public NetworkConnection createUrl(){
            switch (typeRequest){
                case MOST_POPULAR_REQUEST:
                    this.request_url = NYTIMES_URL + typeRequest + mostPopularType + section + "/" + timePeriod + ".json";
                    break;
                case ARCHIVE_REQUEST:
                    this.request_url = NYTIMES_URL + typeRequest + year + "/" + month + ".json";
                    break;
            }
            return new NetworkConnection(this);
        }
    }

    private RequestParams getRequestApiKey(){
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", API_KEY);
        return requestParams;
    }
}
