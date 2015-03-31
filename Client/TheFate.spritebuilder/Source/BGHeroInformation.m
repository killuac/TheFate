//
//  BGHeroInformation.m
//  TheFate
//
//  Created by Killua Liu on 4/9/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHeroInformation.h"
#import "BGConstant.h"

@implementation BGHeroInformation

- (void)didLoadFromCCB
{
    if (IS_IPHONE5) {
        _background.scaleX += _background.scaleX * IPHONE5_ADDITIONAL_SCALEX;
    }
    
//    _textView = [[BGTextView alloc] initWithFrame:CGRectZero];
//    _textView.userInteractionEnabled = NO;
//    _textView.font = [UIFont systemFontOfSize:[self fontSize]];
//    [[CCDirector sharedDirector].view addSubview:_textView];
}

//- (CGFloat)fontSize
//{
//    return (IS_PHONE) ? 11 : 22;
//}
//
//- (void)setUITextViewFrameWithPosition:(CGPoint)glPos
//{
//    CGPoint origin = [[CCDirector sharedDirector] convertToUI:glPos];
//    
//    CGSize size = CGSizeZero;
//    size.width = SCALE_MULTIPLIER * SCREEN_WIDTH;
//    size.height = SCALE_MULTIPLIER * self.bgSize.height;
//    
//    CGRect frame = CGRectZero;
//    frame.origin = origin;
//    frame.size = size;
//    _textView.frame = frame;
//}

- (CGSize)bgSize
{
    CGFloat width = _background.contentSize.width;
    CGFloat height = _background.contentSize.height;
    return CGSizeMake(width*_background.scaleX, height*_background.scaleY);
}

//- (void)setPosition:(CGPoint)position
//{
//    CGPoint offset = ccpSub(position, self.position);
//    [super setPosition:position];
//    
//    _textView.center = ccpSub(_textView.center, ccpMult(ccp(0.0f, offset.y), SCALE_MULTIPLIER));
//}

- (void)setHeroDesc:(NSString *)heroDesc
{
    _textLabel.string = [heroDesc stringByReplacingOccurrencesOfString:@"\n+" withString:@"\n"];
    [_textLabel setTextColor:DESCRIPTION_TEXT_COLOR];
}

- (void)showFromTopInNode:(CCNode *)node
{
//    [self setUITextViewFrameWithPosition:ccp(0.0f, SCREEN_HEIGHT+self.bgSize.height)];
    
    _position = ccp(SCREEN_WIDTH/2, SCREEN_HEIGHT);
    CGPoint targetPos = ccp(SCREEN_WIDTH/2, SCREEN_HEIGHT-self.bgSize.height);
    [node addChild:self z:299];
    
    [self runEaseMoveWithDuration:DURATION_POPUP_MOVE position:targetPos block:nil];
}

- (void)showFromBottomInNode:(CCNode *)node
{
//    [self setUITextViewFrameWithPosition:CGPointZero];
    
    _position = ccp(SCREEN_WIDTH/2, -self.bgSize.height);
    CGPoint targetPos = ccp(SCREEN_WIDTH/2, 0.0f);
    [node addChild:self];
    
    [self runEaseMoveWithDuration:DURATION_POPUP_MOVE position:targetPos block:nil];
}

- (void)removeFromParent
{
    [super removeFromParent];
//    [_textView removeFromSuperview];
}

@end
