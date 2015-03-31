//
//  CCLabelTTF+BGLabelTTF.m
//  TheFate
//
//  Created by Killua Liu on 11/23/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCLabelTTF+BGLabelTTF.h"

@implementation CCLabelTTF (BGLabelTTF)

- (void)setTextColor:(CCColor *)color
{
#if __CC_PLATFORM_IOS
    if ([CCConfiguration sharedConfiguration].OSVersion >= CCSystemVersion_iOS_6_0) {
        NSMutableArray *ranges = [NSMutableArray array];
        [self skillNameRangeFromText:[self.string copy] to:ranges];
        
        NSMutableAttributedString* attString = [[NSMutableAttributedString alloc]initWithString:self.string];
        [attString addAttribute:NSForegroundColorAttributeName
                          value:self.color
                          range:NSMakeRange(0, self.string.length)];
        [attString addAttribute:NSFontAttributeName
                          value:[UIFont systemFontOfSize:self.fontSize]
                          range:NSMakeRange(0, self.string.length)];
        
        for (NSValue *value in ranges) {
            [attString addAttribute:NSForegroundColorAttributeName
                              value:color
                              range:value.rangeValue];
            [attString addAttribute:NSFontAttributeName
                              value:[UIFont boldSystemFontOfSize:self.fontSize]
                              range:value.rangeValue];
        }
        
        self.attributedString = attString;
    }
#endif
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

@end
