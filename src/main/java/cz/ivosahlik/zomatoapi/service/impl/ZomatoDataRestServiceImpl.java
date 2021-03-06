package cz.ivosahlik.zomatoapi.service.impl;

import cz.ivosahlik.zomatoapi.constants.Constants;
import cz.ivosahlik.zomatoapi.service.ZomatoDataRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Intellij Idea
 * Created by ivosahlik on 31/03/2018
 */
@Slf4j
@Service
public class ZomatoDataRestServiceImpl implements ZomatoDataRestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.key}")
    private String apiKey;

    @Value("${rest.api.menu.url}")
    private String dailyMenusByRestaurantIdUrl;

    @Value("${rest.api.restaurant.url}")
    private String restaurantByIdUrl;

    @Value("${rest.api.geocode.url}")
    private String nearbyRestaurantUrl;

    private static final String ACCEPT_HEADER_NAME = "Accept";
    private static final String USER_KEY_HEADER_NAME = "user_key";
    private static final String REST_API_PARAM_ID = "id";
    private static final String REST_API_PARAM_LATITUDE = "lat";
    private static final String REST_API_PARAM_LONGITUDE = "lon";


    /**
     * Return JSON string with daily menu by restaurant id
     *
     * @param restaurantId
     * @return String
     */
    @Cacheable(value = Constants.ZOMATO_CACHE_DAILY_MENUS, key = "#restaurantId.concat('-daily-menu')")
    public String getDailyMenus(String restaurantId) {
        Map<String, String> dailyMenusParams = new HashMap<>();
        dailyMenusParams.put(REST_API_PARAM_ID, restaurantId);

        HttpEntity<?> entity = getHttpEntityForRestRequest();
        String dailyMenusJSONString = null;
        try {
            log.info("dailyMenusParams {} " +  dailyMenusParams);
            dailyMenusJSONString = restTemplate.postForObject(dailyMenusByRestaurantIdUrl, entity, String.class, dailyMenusParams);
        } catch (HttpClientErrorException ex) {
            log.info("Restaurant with ID: {0} has 0 daily menus", restaurantId);
        }
        return dailyMenusJSONString;
    }


    /**
     * Return JSON string with nearby restaurants by latitude and longtitude
     *
     * @param latitude
     * @param longtitude
     * @return
     */
    public String getNearbyRestaurants(String latitude, String longtitude) {
        Map<String, String> nearbyRestauransParam = new HashMap<>();
        nearbyRestauransParam.put(REST_API_PARAM_LATITUDE, latitude);
        nearbyRestauransParam.put(REST_API_PARAM_LONGITUDE, longtitude);

        HttpEntity<?> entity = getHttpEntityForRestRequest();
        String nearbyRestaurants = null;
        nearbyRestaurants = restTemplate.postForObject(nearbyRestaurantUrl, entity, String.class, nearbyRestauransParam);
        return nearbyRestaurants;
    }


    /**
     * Returns JSON string with restaurant by restaurant id
     *
     * @param restaurantId
     * @return String
     */
    public String getRestaurantById(String restaurantId) {
        Map<String, String> map = new HashMap<>();
        map.put(REST_API_PARAM_ID, restaurantId);
        HttpEntity<?> entity = getHttpEntityForRestRequest();
        String restaurantJSONString = null;
        restaurantJSONString = restTemplate.postForObject(restaurantByIdUrl, entity, String.class, map);
        return restaurantJSONString;
    }


    /**
     *  Returns HttpEntity with setted zomato api-key and accept
     *
     * @return HttpEntity
     */
    private HttpEntity<?> getHttpEntityForRestRequest() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT_HEADER_NAME, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(USER_KEY_HEADER_NAME, apiKey);
        return new HttpEntity<>(map, headers);
    }


}
