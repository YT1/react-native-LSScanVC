#iOS
1、导入项目RCTLSScan
2、在主项目info.plist文件中添加
//相机
<key>NSCameraUsageDescription</key>
<string>cameraDesciption</string>
//通讯录
<key>NSContactsUsageDescription</key>
<string>contactsDesciption</string>
//麦克风
<key>NSMicrophoneUsageDescription</key>
<string>microphoneDesciption</string>
//相册
<key>NSPhotoLibraryUsageDescription</key>
<string>photoLibraryDesciption</string>
其中string值可以随意些

#Android
1. 添加项目引用，在android/setting.gradle中:


    include ':react-native-LSScanVC'
    project(':react-native-LSScanVC').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-LSScanVC/android')

2.在android/app/build.gradle中：

    dependencies {

    compile fileTree(dir: "libs", include: ["*.jar"])

    compile project(':react-native-LSScanVC')//二维码扫描
    }


3. 添加package:
*  使用startReactApplication




     //MainActivity
    import android.os.Bundle;
    import com.facebook.react.ReactInstanceManager;
    import com.facebook.react.ReactRootView;
    import com.facebook.react.shell.MainReactPackage;

    import com.dowin.qrcode.BarcodeScannerPackage;//二维码扫描


    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

     @Override
     protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                 .setApplication(getApplication())
                 .setBundleAssetName("index.android.bundle")
                 .setJSMainModuleName("index.android")
                 .addPackage(new MainReactPackage())
                .addPackage(new BarcodeScannerPackage(this))//二维码扫描
                 .setUseDeveloperSupport(true)
                 .setInitialLifecycleState(LifecycleState.RESUMED)
                 .build();

        Bundle options = new Bundle();
        //
        mReactRootView.startReactApplication(mReactInstanceManager, getMainComponentName(), options);
     }



*  setReactNativeHost


     import android.os.Bundle;
     import com.facebook.react.ReactInstanceManager;
     import com.facebook.react.ReactRootView;
     import com.facebook.react.shell.MainReactPackage;

     import com.dowin.qrcode.BarcodeScannerPackage;//二维码扫描

     import com.facebook.react.ReactNativeHost;
     import com.facebook.react.ReactPackage;
     import java.util.Arrays;
     import java.util.List;

     @Override
     protected void onCreate(Bundle savedInstanceState) {

         super.onCreate(savedInstanceState);
         MainApplication application = (MainApplication) getApplication();
                  application.setReactNativeHost(new ReactNativeHost(application) {
                      @Override
                      protected boolean getUseDeveloperSupport() {
                          return false;
                      }
                  
                      @Override
                      protected List<ReactPackage> getPackages() {
                          return Arrays.<ReactPackage>asList(
                                  new MainReactPackage(),
                                  new BarcodeScannerPackage(MainActivity.this),//二维码扫描
                          );
                      }
                  });
     }
     //MainApplication
     public void setReactNativeHost(ReactNativeHost mReactNativeHost) {
         this.mReactNativeHost = mReactNativeHost;
     }


4. AndroidManifest.xml



#使用


    const LSScan = NativeModules.LSScan;

    LSScan.openLsScan().then(r=> {
                    },e=>{
                    });

#



#生成二维码使用规则
#iOS
1、react-native link react-native-LSScanVC
2、import QRCodeApi from  'react-native-LSScanVC/QRCode';
3、
  <QRCodeApi
   style={{height:200,width:200}}
   imageUrl ='https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1876038905,3897565663&fm=116&gp=0.jpg'
   sourcesInfo='13642660825'
    />

iOS新增长按识别功能
   <QRCodeApi
   style={{height:150,width:150}}
  imageUrl ='https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1876038905,3897565663&fm=116&gp=0.jpg'
  sourcesInfo='你好，扫我哦'
   onChange={this._onchange.bind(this)}
                          />

 //长按识别  关键字为"information"
    _onchange(event){
        console.info('成功识别',event.information);
    }

#v 1.0.1

import com.eguma.barcodescanner.BarcodeScannerPackage; 修改为 import com.dowin.qrcode.BarcodeScannerPackage;
添加保存二维码 QRCodeApi.saveImage();

#v 1.0.2
添加条码生成 属性：paymentCode
#v 1.0.3
添加二维码扫描
<LSScanApi
descText="请将下方矩形框对准包装或设备上的二维码进行识别扫描"
onChange={(e)={
console.info(e.information);
}
/>
