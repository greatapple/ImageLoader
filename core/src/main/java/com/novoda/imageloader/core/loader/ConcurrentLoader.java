/**
 * Copyright 2012 Novoda Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novoda.imageloader.core.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.novoda.imageloader.core.LoaderContext;
import com.novoda.imageloader.core.exception.ImageNotFoundException;
import com.novoda.imageloader.core.loader.util.LoaderTask;
import com.novoda.imageloader.core.model.ImageWrapper;

public class ConcurrentLoader implements Loader {
	
	private LoaderContext loaderContext;
	
	public ConcurrentLoader(LoaderContext loaderContext) {
		this.loaderContext = loaderContext;
  }

	@Override
  public void load(ImageView imageView) {
		ImageWrapper w = new ImageWrapper(imageView);
		if(w.getUrl() == null) {
			Log.w("ImageLoader", "You should never call load if you don't set a ImageTag on the view");
			return;
		}
		try {
			setResource(w, w.getLoadingResourceId());
  		new LoaderTask(imageView, loaderContext).execute();
		} catch (ImageNotFoundException inf) {
			setResource(w, w.getNotFoundResourceId());
		} catch (Throwable t) {
			setResource(w, w.getNotFoundResourceId());
		}
  }
	
	private void setResource(ImageWrapper w, int resId) {
		String key = "resource" + resId + w.getHeight() + w.getHeight();
		if(loaderContext.getResBitmapCache().hasBitmap(key)) {
			Bitmap b = loaderContext.getResBitmapCache().get(key);
			if(b != null) {
				w.setBitmap(b);
				return;
			}
		}
		Bitmap b = loaderContext.getBitmapUtil().scaleResourceBitmap(w, resId);
		loaderContext.getResBitmapCache().put(key, b);
		w.setBitmap(b);
  }

}
