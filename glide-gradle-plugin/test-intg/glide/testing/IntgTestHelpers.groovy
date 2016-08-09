package glide.testing


class IntgTestHelpers {
    //TODO  following is not great option - https://discuss.gradle.org/t/testkit-downloading-dependencies/12305
    public static final File testKitGradleHome = new File(System.getProperty('user.home'), '.gradle-testkit')

    boolean  testKitHomeDirExists() { testKitGradleHome.exists() }
}
