//
//  BGLayoutBox.m
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGLayoutBox.h"
#import "BGConstant.h"

@implementation BGLayoutBox

static float roundUpToEven(float f)
{
    return ceilf(f/2.0f) * 2.0f;
}

- (void)layout
{
    _needsLayout = NO;
    
    if (self.direction == CCLayoutBoxDirectionHorizontal)
    {
        // Get the maximum height
        float maxHeight = 0;
        for (CCNode* child in self.children)
        {
            float height = child.contentSizeInPoints.height;
            if (height > maxHeight) maxHeight = height;
        }
        
        // Position the nodes
        float width = 0;
        for (CCNode* child in self.children)
        {
            CGSize childSize = child.contentSizeInPoints;
            
            CGPoint offset = child.anchorPointInPoints;
            CGPoint localPos = ccp(roundf(width), roundf((maxHeight-childSize.height)/2.0f));
            CGPoint position = ccpAdd(localPos, offset);
            
            child.positionType = CCPositionTypePoints;
            if (self.animated) {
                [child runEaseMoveWithDuration:DURATION_CARD_MOVE position:position block:nil]; // Added by Killua
            } else {
                child.position = position;
            }
            
            width += childSize.width;
            width += self.spacing;
        }
        
        // Account for last added increment
        width -= self.spacing;
        if (width < 0) width = 0;
        
        self.contentSizeType = CCSizeTypePoints;
        self.contentSize = CGSizeMake(roundUpToEven(width), roundUpToEven(maxHeight));
    }
    else
    {
        // Get the maximum width
        float maxWidth = 0;
        for (CCNode* child in self.children)
        {
            float width = child.contentSizeInPoints.width;
            if (width > maxWidth) maxWidth = width;
        }
        
        // Position the nodes
        float height = 0;
        for (CCNode* child in self.children)
        {
            CGSize childSize = child.contentSizeInPoints;
            
            CGPoint offset = child.anchorPointInPoints;
            CGPoint localPos = ccp(roundf((maxWidth-childSize.width)/2.0f), roundf(height));
            CGPoint position = ccpAdd(localPos, offset);
            
            child.position = position;
            child.positionType = CCPositionTypePoints;
            
            height += childSize.height;
            height += self.spacing;
        }
        
        // Account for last added increment
        height -= self.spacing;
        if (height < 0) height = 0;
        
        self.contentSizeType = CCSizeTypePoints;
        self.contentSize = CGSizeMake(roundUpToEven(maxWidth), roundUpToEven(height));
    }
}

- (void) addChild:(CCNode *)node animated:(BOOL)animated
{
    self.animated = animated;
    [super addChild:node];
}

@end
