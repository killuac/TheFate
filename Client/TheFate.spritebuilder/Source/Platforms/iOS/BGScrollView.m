//
//  BGScrollView.m
//  FateClient
//
//  Created by Killua Liu on 3/5/14.
//
//

#import "BGScrollView.h"

@implementation BGScrollView

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.showsHorizontalScrollIndicator = NO;
        self.showsVerticalScrollIndicator = NO;
        self.userInteractionEnabled = YES;
        self.scrollEnabled = NO;
    }
    return self;
}

- (void)stopScrollAnimation
{
    for (UIScrollView *view in self.subviews) {
        [view setContentOffset:CGPointZero animated:YES];
    }
}

/*
 * Override touch functions: This allows Cocos2d to process touches
 */
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.nextResponder touchesBegan:touches withEvent:event];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.nextResponder touchesMoved:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.nextResponder touchesEnded:touches withEvent:event];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.nextResponder touchesCancelled:touches withEvent:event];
}

@end
