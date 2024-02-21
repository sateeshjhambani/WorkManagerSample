# WorkManagerSample

The app provides a sample usage of Jetpack's WorkManager API to chain a couple of tasks (workers in WorkManager terms) and use the output response on the UI.

The first is a DownloadWorker that downloads an image from [Unsplash](https://images.unsplash.com), saves it to the app cache, and returns the image URI. This worker is then chained to the second worker called ColorFilterWorker, which takes the original image, applies a colour filter, and returns the filtered image URI.

## References

[Docs](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started)
