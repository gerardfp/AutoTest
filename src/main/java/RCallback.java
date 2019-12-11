import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface RCallback<T> extends Callback<T> {
    @Override
    public abstract void onResponse(Call<T> call, Response<T> response);

    @Override
    public default void onFailure(Call call, Throwable t) {

    }
}
