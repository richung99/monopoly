close all;
heatmapSum = readmatrix('heatmapSum.csv');
heatmapSum = heatmapSum(:, 1:end-1);

figure;
hSum = heatmap(heatmapSum);
colormap('pink');
hSum.Title = {'Total Visits per Square','4 Players, 100 turns, 500 Games'}; % edit title name here
saveas(gcf, '4p_100t_500g_heatmapSum.png'); % edit file name here

heatmapAvg = readmatrix('heatmapAvg.csv');
heatmapAvg = heatmapAvg(:, 1:end-1);

figure;
hAvg = heatmap(heatmapAvg);
hAvg.Title = {'Average Visits per Square','4 Players, 100 turns, 500 Games'}; % edit title name here
colormap('pink');
saveas(gcf, '4p_100t_500g_heatmapAvg.png'); % edit file name here

propertyMatrix = readcell('propertyStats.csv');
propertyNames = propertyMatrix(1, 1:end-1);
propertyStats = propertyMatrix(2:end, 1:end-1);
propertyStats = transpose(propertyStats);
propertyStats = cell2mat(propertyStats);
turns = 1:1:length(propertyStats);

plotColors = ["#57310b"; "#82fff9"; "#ff408f"; "#ff9500"; "#ff1605"; "#edc600"; "#37db00"; "#002bd6"; "#232324"; "#7a45ff"];

figure;
for i = 1:size(propertyStats, 1)
    plot(turns, propertyStats(i,:), 'Color', plotColors(i));
    hold on;
end

lgd = legend(propertyNames, 'Location','southOutside');
lgd.NumColumns = 5;
title(sprintf('Visits by Property Color\n4 Players, 100 turns, 500 games')) % edit title name here
saveas(gcf, '4p_100t_500g_propertyStats.png'); % edit file name here