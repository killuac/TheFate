//
//  BGTextView.m
//  FateClient
//
//  Created by Killua Liu on 3/13/14.
//
//

#import "BGTextView.h"

@implementation BGTextView

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.editable = NO;
        self.userInteractionEnabled = YES;
        self.directionalLockEnabled = YES;
        self.showsVerticalScrollIndicator = NO;
        self.showsHorizontalScrollIndicator = NO;
        self.backgroundColor = [UIColor clearColor];
        self.textColor = [UIColor colorWithRed:0.8 green:0.7 blue:0.5 alpha:1];
    }
    return self;
}

- (BOOL)canBecomeFirstResponder
{
    return NO;
}

- (void)setTextColor:(UIColor *)color font:(UIFont *)font
{
    NSMutableArray *ranges = [NSMutableArray array];
    [self skillNameRangeFromText:[self.text copy] to:ranges];
    
    NSMutableAttributedString* attString = [[NSMutableAttributedString alloc]initWithString:self.text];
    [attString addAttribute:NSForegroundColorAttributeName
                      value:self.textColor
                      range:NSMakeRange(0, self.text.length)];
    [attString addAttribute:NSFontAttributeName
                      value:[UIFont systemFontOfSize:font.pointSize]
                      range:NSMakeRange(0, self.text.length)];
    
    for (NSValue *value in ranges) {
        [attString addAttribute:NSForegroundColorAttributeName
                          value:color
                          range:value.rangeValue];
        [attString addAttribute:NSFontAttributeName
                          value:font
                          range:value.rangeValue];
    }
    
    self.attributedText = attString;
}

- (void)skillNameRangeFromText:(NSString *)text to:(NSMutableArray *)ranges
{
    NSRange endRange = [text rangeOfString:@"]:"];
    if (endRange.location == NSNotFound) return;
    
    NSRange startRange = [text rangeOfString:@"\n" options:NSBackwardsSearch range:NSMakeRange(0, endRange.location)];
    if (startRange.location == NSNotFound) startRange = NSMakeRange(0, 0);
    
    NSRange range = NSMakeRange(startRange.location, endRange.location-startRange.location+1);
    [ranges addObject:[NSValue valueWithRange:range]];
    text = (startRange.length > 0) ? [text stringByReplacingCharactersInRange:startRange withString:@" "] : text;
    text = [text stringByReplacingCharactersInRange:endRange withString:@"  "];
    
    [self skillNameRangeFromText:text to:ranges];
}


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
