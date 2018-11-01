package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

class Article
{
    private String title;
    private String description;
    private String thumbnailSource;

    Article(String title, String description, String thumbnailSource)
    {
        this.title = title;
        this.description = description;
        this.thumbnailSource = thumbnailSource;
    }

    String getTitle()
    {
        return title;
    }

    void setTitle(String title)
    {
        this.title = title;
    }

    String getThumbnailSource()
    {
        return thumbnailSource;
    }

    void setThumbnailSource(String thumbnailSource)
    {
        this.thumbnailSource = thumbnailSource;
    }

    String getDescription()
    {
        return description;
    }

    void setDescription(String description)
    {
        this.description = description;
    }
}
