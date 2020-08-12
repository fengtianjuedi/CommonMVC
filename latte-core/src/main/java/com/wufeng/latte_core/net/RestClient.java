package com.wufeng.latte_core.net;

import android.content.Context;

import com.wufeng.latte_core.loader.Loader;

import java.io.File;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.observable.ObservableCreate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RestClient {
    private final String URL;
    private final WeakHashMap<String, Object> PARAMS;
    private final RequestBody BODY;
    private final File FILE;
    private final Context CONTEXT;
    private final ISuccess SUCCESS;
    private final IError ERROR;

    public RestClient(String url, WeakHashMap<String, Object> params, RequestBody body, File file, Context context, ISuccess iSuccess, IError iError){
        this.URL = url;
        this.PARAMS = params;
        this.BODY = body;
        this.FILE = file;
        this.CONTEXT = context;
        this.SUCCESS = iSuccess;
        this.ERROR = iError;
    }

    public static RestClientBuilder builder() {return new RestClientBuilder();}

    private void request(HttpMethod method){
        final RestService service = RestCreator.getRestService();
        Observable<String> observable = null;
        if (CONTEXT != null)
            Loader.showLoading(CONTEXT);
        switch (method){
            case GET:
                observable = service.get(URL, PARAMS);
                break;
            case POST:
                observable = service.post(URL, PARAMS);
                break;
            case POST_RAW:
                observable = service.postRaw(URL, BODY);
                break;
            case PUT:
                observable = service.put(URL, PARAMS);
                break;
            case PUT_RAW:
                observable = service.putRaw(URL, BODY);
                break;
            case DELETE:
                observable = service.delete(URL, PARAMS);
                break;
            case UPLOAD:
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()), FILE);
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", FILE.getName(), requestBody);
                observable = service.upload(URL, body);
                break;
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    private Disposable mDisposable;
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        Loader.stopLoading();
                        mDisposable.dispose();
                        if (SUCCESS != null)
                            SUCCESS.onSuccess(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Loader.stopLoading();
                        if (ERROR != null)
                            ERROR.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        Loader.stopLoading();
                    }
                });
    }

    public final void get() {request(HttpMethod.GET);}

    public final void post() {
        if (BODY == null) {
            request(HttpMethod.POST);
        } else {
            request(HttpMethod.POST_RAW);
        }
    }

    public final void put() {
        if (BODY == null) {
            request(HttpMethod.PUT);
        } else {
            if (PARAMS.isEmpty()) {
                throw new NullPointerException("parmas must not be Empty!");
            }
            request(HttpMethod.PUT_RAW);
        }
    }

    public final void delete() {
        request(HttpMethod.DELETE);
    }

    public final void upload() {
        request(HttpMethod.UPLOAD);
    }

    public final Observable<ResponseBody> download() {
        return RestCreator.getRestService().download(URL, PARAMS);
    }
}
