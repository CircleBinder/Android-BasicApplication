package circlebinder.common.circle;

import android.content.Context;
import android.util.AttributeSet;

import net.ichigotake.common.rx.Binder;

import circlebinder.common.event.Circle;
import circlebinder.common.web.WebView;
import circlebinder.common.web.WebViewPresenter;

public class CircleWebView extends WebView implements Binder<Circle> {

    private final WebViewPresenter presenter;

    @SuppressWarnings("unused") // Public API
    public CircleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        presenter = new WebViewPresenter(this);
    }

    @Override
    public void bind(Circle circle) {
        loadUrl(getCircleLink(circle));
    }

    private String getCircleLink(Circle circle) {
        if (circle.getLinks().isEmpty()) {
            return "https://google.co.jp/search?q="
                    + "\"" + circle.getPenName() + "\""
                    + "%20"
                    + "\"" + circle.getName() + "\"";
        }
        return circle.getLinks().get(0).getUri().toString();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.webViewAttached();
    }

    @Override
    public void destroy() {
        presenter.webViewDetached();
        super.destroy();
    }

}
