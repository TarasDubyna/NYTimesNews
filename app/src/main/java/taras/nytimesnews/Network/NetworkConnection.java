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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.MediaParam;

public class NetworkConnection extends AsyncHttpClient{

    // url example https://api.nytimes.com/svc/  mostpopular/v2/   mostemailed/ Arts/ 1.json?api-key=2586ad6dfc1e4956bf10a11517bd878d
    public final static String NYTIMES_URL = "https://api.nytimes.com/svc/";

    public final static String API_KEY = "2586ad6dfc1e4956bf10a11517bd878d";


    public final static String MOST_MAILED = "mostemailed/";
    public final static String MOST_SHARED = "mostshared/";
    public final static String MOST_VIEWED = "mostviewed/";

    public final static String MOST_POPULAR_REQUEST = "mostpopular/v2/";
    public final static String SEARCH_REQUEST = "search/v2/";
    public final static String ARCHIVE_REQUEST = "archive/v1/";

    public final static String PAGE = "page";
    public final static String NY_BEGIN_DATE = "begin_date";
    public final static String NY_NEWS_DESK = "fq";
    public final static String NY_SORT_ORDER = "sort";
    public final static String QUERY = "q";


    private String request_url = null;

    private String searchText = null;

    private String typeRequest = "mostpopular/v2/";
    private String mostPopularType = MOST_MAILED;
    private String section = "Arts";
    private int timePeriod = 1;

    private int year = 0;
    private int month = 0;

    private NetworkConnection(BuildRequestParam builder){
        this.searchText = builder.searchText;
        request_url = builder.request_url;
    }


    public RequestHandle createRequest(ResponseHandlerInterface responseHandler, String typeRequest)
    {
        if (typeRequest.equals(MOST_POPULAR_REQUEST)){
            return super.get(request_url, getRequestParamsMostPopular(), responseHandler);
        } else {
            return super.get(request_url, getRequestParamsMostPopular(), responseHandler);
        }

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

        //search param
        private String searchText = null;

        public BuildRequestParam mostPopularParams(String mostPopularType, String section, int timePeriod){
            this.typeRequest = MOST_POPULAR_REQUEST;
            this.mostPopularType = mostPopularType;
            this.section = section;
            this.timePeriod = timePeriod;
            return this;
        }
        public BuildRequestParam archiveParams(int year, int month){
            this.typeRequest = ARCHIVE_REQUEST;
            this.year = year;
            this.month = month;
            return this;
        }
        public BuildRequestParam searchParams(String searchText){
            this.typeRequest = SEARCH_REQUEST;
            this.searchText = searchText;
            return this;
        }

        public NetworkConnection createUrl(){
            switch (typeRequest){
                case MOST_POPULAR_REQUEST:
                    this.request_url = NYTIMES_URL + typeRequest + mostPopularType + section + "/" + timePeriod + ".json";
                    break;
                case ARCHIVE_REQUEST:
                    this.request_url = NYTIMES_URL + typeRequest + year + "/" + month + ".json";
                    System.out.println(this.request_url);
                    break;
                case SEARCH_REQUEST:
                    this.request_url = NYTIMES_URL + SEARCH_REQUEST + "articlesearch.json";
                    break;
            }
            return new NetworkConnection(this);
        }
    }

    private RequestParams getRequestParamsMostPopular(){
     RequestParams requestParams = new RequestParams();
     requestParams.put("api-key", API_KEY);
     return requestParams;
    }


    private RequestParams getRequestParamsSearch(String searchText){
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", API_KEY);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - 7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        requestParams.put(NY_BEGIN_DATE, dateFormat.format(calendar.getTime()));
        requestParams.put(NY_SORT_ORDER, "newest");
        requestParams.put(PAGE, 0);
        requestParams.put(QUERY, searchText);


        return requestParams;
    }
}
