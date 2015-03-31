//
//  BGPopup.h
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"
#import "BGBubble.h"
#import "CCTextField+BGTextField.h"

@class BGPopup;

@protocol BGPopupDelegate <NSObject>

@optional
- (void)popupOkay:(BGPopup *)popup;
- (void)popupCancel:(BGPopup *)popup;

- (void)didShowPopup:(BGPopup *)popup;
- (void)didDismissPopup:(BGPopup *)popup;

@end

@interface BGPopup : CCNode <BGPlatformTextFieldDelegate, CCBAnimationManagerDelegate> {
    CCSprite *_background;
    CCLabelBMFont *_titleLabel;
    CCLabelTTF *_messageLabel;
    CCButton *_okayButton;
}

@property (nonatomic, weak) id<BGPopupDelegate> delegate;
@property (nonatomic, copy) NSString *message;
@property (nonatomic) BOOL isKeyboardAppeared;

@property (nonatomic, strong, readonly) id runningSceneNode;
@property (nonatomic, strong, readonly) CCSprite *background;
@property (nonatomic, readonly) CGRect bgBoundingBox;
@property (nonatomic, readonly) CGSize bgSize;

- (void)didLoadFromCCB;
- (void)setTitleLabel;
- (void)setAllUIViewsHidden:(BOOL)hidden;
- (void)show;
- (void)showInNode:(id)parentNode;
- (void)dismiss;

- (void)showWrongPrompt:(EsObject *)esObj;
- (void)showBubbleWithText:(NSString *)text inNode:(CCNode *)node;

- (void)okay:(CCButton *)sender;
- (void)cancel:(CCButton *)sender;

@end
