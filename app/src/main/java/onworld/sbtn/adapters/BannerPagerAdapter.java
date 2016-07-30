package onworld.sbtn.adapters;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.BannerItems;
import onworld.sbtn.utils.ImageUtils;

/**
 * Created by onworldtv on 11/18/15.
 */
public class BannerPagerAdapter extends PagerAdapter {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<BannerItems> bannerItemses = new ArrayList<>();
    private String[] arrImage = new String[0];
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ChangeBannerListener changeBannerListener;
    private Context context;

    public BannerPagerAdapter(Context context, String[] arrImage, ArrayList<BannerItems> bannerItemses) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.arrImage = arrImage;
        this.bannerItemses = bannerItemses;
        options = ImageUtils.getOptionsImageRectangle();
        if (!imageLoader.isInited()) {
            initImageLoader();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (arrImage != null) {
            return arrImage.length;
        } else {
            return 0;
        }

    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.banner_slideshow, view, false);
        assert imageLayout != null;
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image_banner);

        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

        imageLoader.displayImage(arrImage[position], imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
        });

        view.addView(imageLayout, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBannerListener.onChangeBannerListener(position, bannerItemses);

            }
        });

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void setChangeBannerListener(ChangeBannerListener listener) {
        this.changeBannerListener = listener;
    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                //.memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();

        ImageLoader.getInstance().init(config);
    }

    public interface ChangeBannerListener {
        void onChangeBannerListener(int position, ArrayList<BannerItems> bannerItemses);
    }
}
