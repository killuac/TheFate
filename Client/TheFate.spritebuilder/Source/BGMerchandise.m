//
//  BGMerchandise.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGMerchandise.h"
#import "BGMacro.h"
#import "BGConstant.h"

@implementation BGMerchandise

/*
 * "didLoadFromCCB" will be called twice while calling [CCBReader load]
 * So need create textView in this init method
 */
//- (id)init
//{
//    if (self = [super init]) {
//        _textView = [[BGTextView alloc] initWithFrame:CGRectZero];
//        _textView.font = [UIFont systemFontOfSize:[self fontSize]];
//    }
//    return self;
//}
//
//- (CGFloat)fontSize
//{
//    return (IS_PHONE) ? 7 : 14;
//}
//
//- (void)setPosition:(CGPoint)position
//{
//    [super setPosition:position];
//    [self setUITextViewFrame];
//}

//- (void)setUITextViewFrame
//{
//    CGPoint origin = [[CCDirector sharedDirector] convertToUI:self.position];
//    
//    CGSize size = self.contentSizeInPoints;
//    size.width *= (IS_PAD) ? SCALE_MULTIPLIER : SCALE_MULTIPLIER * 1.05f;
//    size.height *= SCALE_MULTIPLIER * 0.3f;
//    
//    CGRect frame = CGRectZero;
//    frame.origin = origin;
//    frame.size = size;
//    _textView.frame = frame;
//}

- (void)setMerchandiseDesc:(NSString *)merchandiseDesc
{
    CCLabelTTF *textLabel = (CCLabelTTF *)_textView.contentNode.children.lastObject;
    merchandiseDesc = [merchandiseDesc stringByReplacingOccurrencesOfString:@"\\n" withString:@"\n"];
    textLabel.string = [merchandiseDesc stringByReplacingOccurrencesOfString:@"\n+" withString:@"\n"];
}

- (void)setImageName:(NSString *)imageName
{
     imageName = [NSString stringWithFormat:@"Merchandise/%@.png", imageName];
    _imageSprite.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:imageName];
}

@end
