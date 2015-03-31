1. Cocos2D framework source code was changed by Killua Liu:
CCDirectorIOS:  [_displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
CCPlatformTextFieldIOS: Add @property (nonatomic, strong) UITextField *textField;
CCPlatformTextFieldAndroid: [_editText setTextColorByColor:AndroidColorGRAY];

2.
HeroName: lineHeight=55
HeroName4: lineHeight=45
EquipmentName: lineHeight=50
EquipmentName4: lineHeight=40


3. Change compiler flag: Targets -> Build Phases
-fobjc-arc: With ARC
-fno-objc-arc: Without ARC


4. With current Xcode, you can make use of the z and t modifiers to handle NSInteger and NSUInteger without warnings, on all architectures.
You want to use %zd for signed, %tu for unsigned, and %tx for hex.
