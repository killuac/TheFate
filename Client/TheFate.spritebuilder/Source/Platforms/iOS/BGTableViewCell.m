//
//  BGTableViewCell.m
//  TheFate
//
//  Created by Killua Liu on 4/12/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGTableViewCell.h"
#import "BGRoomScene.h"

@implementation BGTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGSize size = self.bounds.size;
    CGRect frame = self.textLabel.frame;
    CGFloat width = (self.detailTextLabel) ? size.width*0.2f : size.width;
    CGFloat xPos, yPos;
    CGFloat height = (IS_PHONE) ? 15.0f : frame.size.height;
    
    id runningSceneNode = [CCDirector sharedDirector].runningScene.children.lastObject;
    if ([runningSceneNode chatPopup]) {
        yPos = (IS_PHONE) ? 2.0f : MAX(frame.origin.y, 8.0f);
        if (IS_PHONE && size.height > 20.0f) yPos = 6.0f;       // Multiple line
    } else {
        yPos = (IS_PHONE) ? 0.0f : MAX(frame.origin.y, 3.0f);
        if (IS_PHONE && size.height > 20.0f) yPos = 2.0f;       // Multiple line
    }
    xPos = (IS_PHONE) ? 6.0f : frame.origin.x;
    self.textLabel.frame = CGRectMake(xPos, yPos, width, height);
    
    frame = self.detailTextLabel.frame;
    yPos = (IS_PHONE) ? 0.0f : frame.origin.y;
    height = (IS_PHONE) ? size.height : frame.size.height;
    self.detailTextLabel.frame = CGRectMake(xPos+size.width*0.21f, yPos, size.width*DETAIL_LABEL_PERCENTAGE, height);
}

@end
