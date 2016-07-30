package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import onworld.sbtn.R;

/**
 * Created by onworldtv on 11/13/15.
 */
public class ImageSlidePagerAdapter extends PagerAdapter {


    private String[] arrImage;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ImageButton nextImage, previousImage;
    private ChangeImageSlideListener changeImageSlideListener;
    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.next_image:
                    changeImageSlideListener.onNextImageSlideListener();
                    break;
                case R.id.previous_image:
                    changeImageSlideListener.onPreviousImageSlideListener();
                    break;
            }
        }
    };
    private boolean isSlidePhoto;
    private ViewPhotoListener viewPhotoListener;

    public ImageSlidePagerAdapter(Context context, String[] arrImage, boolean isSlidePhoto) {
        inflater = LayoutInflater.from(context);
        this.arrImage = arrImage;
        this.isSlidePhoto = isSlidePhoto;

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
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
        View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
        assert imageLayout != null;
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        nextImage = (ImageButton) imageLayout.findViewById(R.id.next_image);
        previousImage = (ImageButton) imageLayout.findViewById(R.id.previous_image);
        nextImage.setOnClickListener(myClickListener);
        previousImage.setOnClickListener(myClickListener);

        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

        ImageLoader.getInstance().displayImage(arrImage[position], imageView, options, new SimpleImageLoadingListener() {
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
                if (isSlidePhoto == true) {
                    viewPhotoListener.onViewPhotoListener(position, arrImage);
                }

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

    public void setChangeImageSlideListener(ChangeImageSlideListener listener) {
        this.changeImageSlideListener = listener;
    }

    public void setViewPhotoListener(ViewPhotoListener listener) {
        this.viewPhotoListener = listener;
    }

    public interface ChangeImageSlideListener {
        void onNextImageSlideListener();

        void onPreviousImageSlideListener();
    }

    public interface ViewPhotoListener {
        void onViewPhotoListener(int position, String[] url);
    }

}