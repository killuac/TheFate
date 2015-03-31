//
//  BGPopup.m
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"
#import "BGLoginScene.h"

@implementation BGPopup

- (void)didLoadFromCCB
{
    CCAnimationManager *animationManager = self.userObject;
    animationManager.delegate = self;
    
    if (_titleLabel) [self setTitleLabel];
}

- (void)setTitleLabel
{
    _titleLabel.scale /= FONT_SCALE_FACTOR;    
    _titleLabel.scaleX /= MAX(_titleLabel.parent.scaleX, 1);
    _titleLabel.scaleY /= MAX(_titleLabel.parent.scaleY, 1);
}

- (void)setMessage:(NSString *)message
{
    _messageLabel.string = message;
}

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

- (CGSize)bgSize
{
    CGFloat width = _background.contentSize.width;
    CGFloat height = _background.contentSize.height;
    return CGSizeMake(width*_background.scaleX, height*_background.scaleY);
}

- (CGRect)bgBoundingBox
{
    CGFloat x = _background.positionInPoints.x;
    CGFloat y = _background.positionInPoints.y;
    CGFloat width = _background.contentSize.width;
    CGFloat height = _background.contentSize.height;
    return CGRectMake(x, y, width, height);
}

- (void)setAllUIViewsHidden:(BOOL)hidden
{
#if __CC_PLATFORM_IOS
    for (UIView *view in [CCDirector sharedDirector].view.subviews) {
        if ([view isKindOfClass:[UIView class]]) {
            view.hidden = hidden;
        }
    }
#endif
}

- (void)show
{
    [self showInNode:self.runningSceneNode];
}

- (void)showInNode:(id)parentNode
{
    [parentNode removeLoading];
    if ([parentNode respondsToSelector:@selector(effectNode)]) {
        [[parentNode effectNode] blurEffect];
    }
    
    self.position = SCREEN_CENTER;
    self.delegate = parentNode;
    [parentNode addChild:self z:1000];
}

- (void)dismiss
{
    CCAnimationManager *animationManager = self.userObject;
    [animationManager runAnimationsForSequenceNamed:@"Dismiss"];
}

- (void)completedAnimationSequenceNamed:(NSString *)name
{
    if ([name isEqualToString:@"Show"]) {
        [self setAllUIViewsHidden:NO];
        if ([_delegate respondsToSelector:@selector(didShowPopup:)]) [_delegate didShowPopup:self];
    } else {
        if ([_delegate respondsToSelector:@selector(didDismissPopup:)]) [_delegate didDismissPopup:self];
        [self removeFromParent];
    }
}

- (void)showWrongPrompt:(EsObject *)esObj
{
    
}

- (void)showBubbleWithText:(NSString *)text inNode:(CCNode *)node
{
    BGBubble *bubble = (BGBubble *)[CCBReader load:kCcbiBubble];
    bubble.positionType = CCPositionTypeNormalized;
    bubble.position = ccp(1.0f, 1.0f);
    [bubble setMessage:text];
    [bubble showInNode:node];
}

- (void)okay:(CCButton *)sender
{
    sender.enabled = NO;
    [self setAllUIViewsHidden:YES];
    if ([_delegate respondsToSelector:@selector(popupOkay:)]) [self.delegate popupOkay:self];
    [self dismiss];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)cancel:(CCButton *)sender
{
    sender.enabled = NO;
    [self setAllUIViewsHidden:YES];
    if ([_delegate respondsToSelector:@selector(popupCancel:)]) [self.delegate popupCancel:self];
    [self dismiss];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidBeginEditing:(CCPlatformTextField *)platformTextField
{
//  Implement in subclasses
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
//  Implement in subclasses
}

@end
