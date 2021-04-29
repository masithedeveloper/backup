/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework.ssl;

import android.text.TextUtils;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.CacheControl;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0617_ONCE_OFF_AIRTIME;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0618_ONCE_OFF_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0619_ONCE_OFF_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0620_BUY_AIRTIME;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0621_BUY_AIRTIME_CONFIRM;
import static com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0622_BUY_AIRTIME_RESULT;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0335_VALIDATE_METER_NUMBER;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY;
import static com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0339_PURCHASE_PREPAID_ELECTRICITY;
import static com.barclays.absa.banking.debiCheck.services.dto.DebiCheckTransactionsRequest.OP2102_FETCH_DEBICHECK_TRANSACTIONS;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0412_AIRTIME_TRANSACT;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0890_APPLY_REWARDS;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0891_APPLY_REWARDS_CONFIRM;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0892_APPLY_REWARDS_RESULT;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0916_VIEW_REWARDS_MEMBERSHIP_DETAILS;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0919_REWARDS_DETAILS;
import static com.barclays.absa.banking.home.services.HomeScreenService.OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY;
import static com.barclays.absa.banking.newToBank.services.NewToBankService.OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2006_OVERDRAFT_QUOTE_DETAILS;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2008_OVERDRAFT_ACCEPT_QUOTE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2009_OVERDRAFT_REJECT_QUOTE;
import static com.barclays.absa.banking.overdraft.services.OverdraftService.OP2013_OVERDRAFT_QUOTE_SUMMARY;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0868_PERFORM_INTERNATIONAL_PAYMENT;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0872_GET_QUOTE_DETAILS;
import static com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.OP0884_VALIDATE_PAYMENT;
import static com.barclays.absa.banking.payments.services.PaymentsService.OP2195_FETCH_EXERGY_BANK_DETAILS;
import static com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM;
import static com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0913_REDEEM_REWARDS_RESULT;

public final class OkHttpConnectorServiceImpl implements OkHttpConnectorService {
    private static boolean logHttpDebugInformation = false;

    private static final String TAG = "OkHttpConnectorService";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final CookieStore COOKIE_STORE = new CookieStore();
    private static final OkHttpConnectorService FAST_HTTP_SERVICE = new OkHttpConnectorServiceImpl();
    private boolean isSureCheckPollingRequest;
    private boolean isLongRunningRequest;

    public static OkHttpConnectorService getInstance() {
        return FAST_HTTP_SERVICE;
    }

    private boolean isSureCheckPollingRequest(String opCode) {
        return "OP0812".equalsIgnoreCase(opCode) || "OP0814".equalsIgnoreCase(opCode);
    }

    private boolean isLongRunningRequest(String opCode) {
        if (opCode == null) {
            return false;
        }

        switch (opCode.toUpperCase()) {
            case OP0335_VALIDATE_METER_NUMBER:
            case OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY:
            case OP0339_PURCHASE_PREPAID_ELECTRICITY:
            case OP0412_AIRTIME_TRANSACT:
            case OP0617_ONCE_OFF_AIRTIME:
            case OP0618_ONCE_OFF_AIRTIME_CONFIRM:
            case OP0619_ONCE_OFF_AIRTIME_RESULT:
            case OP0620_BUY_AIRTIME:
            case OP0621_BUY_AIRTIME_CONFIRM:
            case OP0622_BUY_AIRTIME_RESULT:
            case OP0868_PERFORM_INTERNATIONAL_PAYMENT:
            case OP0872_GET_QUOTE_DETAILS:
            case OP0884_VALIDATE_PAYMENT:
            case OP0890_APPLY_REWARDS:
            case OP0891_APPLY_REWARDS_CONFIRM:
            case OP0892_APPLY_REWARDS_RESULT:
            case OP0912_REDEEM_REWARDS_CONFIRM:
            case OP0913_REDEEM_REWARDS_RESULT:
            case OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY:
            case OP0916_VIEW_REWARDS_MEMBERSHIP_DETAILS:
            case OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE:
            case OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT:
            case OP0919_REWARDS_DETAILS:
            case OP2006_OVERDRAFT_QUOTE_DETAILS:
            case OP2008_OVERDRAFT_ACCEPT_QUOTE:
            case OP2009_OVERDRAFT_REJECT_QUOTE:
            case OP2013_OVERDRAFT_QUOTE_SUMMARY:
            case OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE:
            case OP2195_FETCH_EXERGY_BANK_DETAILS:
            case OP2102_FETCH_DEBICHECK_TRANSACTIONS:
                return true;
            default:
                return false;
        }
    }

    public String callHttpService(String serviceUrl, String operationCode, Map<String, String> params) {
        if (TextUtils.isEmpty(serviceUrl))
            serviceUrl = BuildConfigHelper.INSTANCE.getServerPath();

        HttpUrl httpUrl = HttpUrl.parse(serviceUrl);
        try {
            isSureCheckPollingRequest = isSureCheckPollingRequest(operationCode);
            isLongRunningRequest = isLongRunningRequest(operationCode);
            OkHttpClient okHttpClient = httpUrl.isHttps() && HttpUtils.shouldUseSSLConnector() ? getSecureHttpClient() :
                    getPlainHttpClient();


            okhttp3.Request okHttpRequest;
            if (params.size() == 0) {
                okHttpRequest = new okhttp3.Request.Builder()
                        .url(httpUrl)
                        .get()
                        .build();
            } else {
                RequestBody requestBody = buildRequestBody(params);
                okHttpRequest = new okhttp3.Request.Builder()
                        .url(httpUrl)
                        .post(requestBody)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .header("Accept", MEDIA_TYPE.toString())
                        .addHeader("token", operationCode)
                        .build();
            }

            Response response = okHttpClient.newCall(okHttpRequest).execute();
            logHttpTraffic(okHttpRequest, response);
            return response.isSuccessful() ? response.body().string() : null;

        } catch (UnknownHostException e) {
            return BMBConstants.NETWORK_UNREACHABLE;
        } catch (SecurityException e) {
            return e.getLocalizedMessage();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Oops, Error communicating with Barclays Mobile Gateway", e);
            }
            String connTimeoutMessage = BMBApplication.getApplicationLocale().getLanguage().equalsIgnoreCase(BMBConstants.ENGLISH_CODE) ? BMBConstants.CONN_TIME_OUT : BMBConstants.CONN_TIME_OUT_AF;
            if (e instanceof SSLHandshakeException) {
                return AppConstants.SSLPinningError;
            } else if (e instanceof SocketTimeoutException) {
                return connTimeoutMessage;
            } else {
                new MonitoringInteractor().logTechnicalEvent(OkHttpConnectorServiceImpl.class.getSimpleName(), operationCode, String.format("IOException: %s", e.getMessage()));
                return (BMBApplication.getApplicationLocale().getLanguage().equalsIgnoreCase(BMBConstants.ENGLISH_CODE)) ? AppConstants.GENERIC_ERROR_MSG : AppConstants.GENERIC_ERROR_MSG_AF;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void logHttpTraffic(okhttp3.Request request, Response response) {
        if (BuildConfig.DEBUG && logHttpDebugInformation) {
            Handshake handshake = response.handshake();

            StringBuilder logBuilder = new StringBuilder();
            FormBody formBody = (FormBody) request.body();

            if (formBody != null) {
                logBuilder.append("--- Raw Request ---").append("\n").append("\n").append(buildJsonString(formBody)).append("\n");
            }

            logBuilder.append("\n").append("Handshake:").append("\n");
            logBuilder.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").append("\n");
            if (handshake != null) {
                logBuilder.append(String.format("%-35s", "Cipher Suite")).append(" = ").append(handshake.cipherSuite().javaName()).append("\n");
                logBuilder.append(String.format("%-35s", "TLS Version")).append(" = ").append(handshake.tlsVersion().javaName()).append("\n");
            }

            Headers requestHeaders = request.headers();
            logBuilder.append("\n").append("Request Headers:").append("\n");
            logBuilder.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").append("\n");
            for (int i = 0; i < requestHeaders.size(); i++) {
                logBuilder.append(String.format("%-35s", requestHeaders.name(i))).append(" = ").append(requestHeaders.value(i)).append("\n");
            }

            Log.d(TAG, logBuilder.toString());
            logBuilder = new StringBuilder();
            logBuilder.append(String.format("%-35s", "dummy")).append(" = ").append("output").append("\n");

            logBuilder.append("\n").append("Request Body:").append("\n");
            logBuilder.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").append("\n");

            if (formBody != null) {
                for (int i = 0; i < formBody.size(); i++) {
                    logBuilder.append(String.format("%-35s", formBody.encodedName(i))).append(" = ").append(formBody.encodedValue(i)).append("\n");
                }
            }

            Log.d("", logBuilder.toString());
            logBuilder = new StringBuilder();
            logBuilder.append(String.format("%-35s", "dummy")).append(" = ").append("output").append("\n");

            Headers responseHeaders = response.headers();
            logBuilder.append("\n").append("Response Headers:").append("\n");
            logBuilder.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").append("\n");

            for (int i = 0; i < responseHeaders.size(); i++) {
                logBuilder.append(String.format("%-35s", responseHeaders.name(i))).append(" = ").append(responseHeaders.value(i)).append("\n");
            }

            logBuilder.append("\n").append("Response Status:").append("\n");
            logBuilder.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").append("\n");
            logBuilder.append(String.format("%-35s", "Response Code")).append(" = ").append(response.code()).append("\n");
            logBuilder.append(String.format("%-35s", "Response Message")).append(" = ").append(response.message()).append("\n");
            logBuilder.append(String.format("%-35s", "Response Latency")).append(" = ").append((response.receivedResponseAtMillis() - response.sentRequestAtMillis())).append("\n");
            logBuilder.append("****************************************************************************************************************************************************************************************************");

            Log.d("", logBuilder.toString());
        }
    }

    private String buildJsonString(FormBody formBody) {
        StringBuilder output = new StringBuilder();
        output.append("{");
        for (int i = 0; i < formBody.size(); i++) {
            output.append("\"").append(formBody.encodedName(i)).append("\"").append(":").append("\"").append(formBody.encodedValue(i)).append("\"").append(", ");
        }
        output.delete(output.length() - 2, output.length());
        output.append("}");
        return output.toString();
    }

    private RequestBody buildRequestBody(Map<String, String> params) {
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || key.equals(""))
                continue;
            if (value == null)
                value = "";
            requestBodyBuilder.add(key, value);
        }
        return requestBodyBuilder.build();
    }

    private OkHttpClient.Builder getBaseHttpClientBuilder() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        configureTimeoutPolicy(okHttpClientBuilder);
        configureCookies(okHttpClientBuilder);
        return okHttpClientBuilder;
    }

    private void configureTimeoutPolicy(OkHttpClient.Builder okHttpClientBuilder) {
        if (isLongRunningRequest) {
            final int prepaidTandemTimeout = 90;
            okHttpClientBuilder.connectTimeout(prepaidTandemTimeout, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(prepaidTandemTimeout, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(prepaidTandemTimeout, TimeUnit.SECONDS);
        } else if (isSureCheckPollingRequest) {
            okHttpClientBuilder.connectTimeout(45, TimeUnit.SECONDS);
        } else {
            okHttpClientBuilder.connectTimeout(HttpUtils.CONNECT_TIMEOUT, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(HttpUtils.WRITE_TIMEOUT, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(HttpUtils.READ_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    private void configureCookies(OkHttpClient.Builder okHttpClientBuilder) {
        okHttpClientBuilder.cookieJar(COOKIE_STORE);
    }

    private OkHttpClient getPlainHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = getBaseHttpClientBuilder();
        return okHttpClientBuilder.build();
    }

    private OkHttpClient getSecureHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = getBaseHttpClientBuilder();
        configureConnectionSpecs(okHttpClientBuilder);
        if (BuildConfig.PRD || BuildConfig.PRD_BETA || BuildConfig.ENABLE_CERT_PINNING) {
            configureCertificatePinningPolicy(okHttpClientBuilder);
        }
        return okHttpClientBuilder.build();
    }

    private void configureConnectionSpecs(OkHttpClient.Builder okHttpClientBuilder) {
        ConnectionSpec nCipherConnectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .supportsTlsExtensions(true)
                .build();

        ConnectionSpec bmgConnectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .supportsTlsExtensions(true)
                .build();

        List<ConnectionSpec> connectionSpecs = new ArrayList<>();
        connectionSpecs.add(nCipherConnectionSpec);
        connectionSpecs.add(bmgConnectionSpec);
        okHttpClientBuilder.connectionSpecs(connectionSpecs);
    }

    private static void configureCertificatePinningPolicy(OkHttpClient.Builder okHttpClientBuilder) {
        OkHttpSSLSocketFactory sslSocketFactory = new OkHttpSSLSocketFactory();
        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager());
    }

    private static class CookieStore implements CookieJar {
        private final Set<Cookie> cookies = new HashSet<>();

        @Override
        public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
            this.cookies.addAll(cookies);
        }

        void clearCookies() {
            if (!cookies.isEmpty()) {
                cookies.clear();
            }
        }

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
            List<Cookie> validCookies = new ArrayList<>();
            for (Cookie cookie : cookies) {
                logSessionCookie(cookie);
                if (cookie.expiresAt() < System.currentTimeMillis()) {
                    continue;
                }
                validCookies.add(cookie);
            }
            return validCookies;
        }

        private void logSessionCookie(Cookie cookie) {
            if (BuildConfig.DEBUG && logHttpDebugInformation) {
                Log.d(TAG, "Session cookie:");
                Log.d(TAG, "--------------------------------------------------------------------------------------------------");
                Log.d(TAG, String.format("%-35s", "Cookie") + " = " + cookie.toString());
                Log.d(TAG, String.format("%-35s", "Expires") + " = " + cookie.expiresAt());
                Log.d(TAG, String.format("%-35s", "Hash") + " = " + cookie.hashCode());
                Log.d(TAG, String.format("%-35s", "Path") + " = " + cookie.path());
                Log.d(TAG, String.format("%-35s", "Domain") + " = " + cookie.domain());
                Log.d(TAG, String.format("%-35s", "Name") + " = " + cookie.name());
                Log.d(TAG, String.format("%-35s", "Value") + " = " + cookie.value());
                Log.d(TAG, "**************************************************************************************************");
                Log.d("-", " ");
            }
        }
    }

    @Override
    public void clearSessionCookies() {
        COOKIE_STORE.clearCookies();
    }
}
