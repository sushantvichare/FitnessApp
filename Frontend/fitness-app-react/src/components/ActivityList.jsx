import { Card, CardContent, Grid, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";

import { useNavigate } from "react-router";
import { getActivities } from "../services/api.js";

function ActivityList() {
  const [activities, setActivities] = useState([]);

  const navigate = useNavigate();

  const fetchActivities = async () => {
    try {
      const response = await getActivities();
      setActivities(response.data);
    } catch (error) {
      console.error("Error fetching activities:", error);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchActivities();
  }, []);

  return (
    <Grid container spacing={2}>
      {activities.map((activity) => (
        <Grid
          container
          spacing={{ xs: 2, md: 3 }}
          columns={{ xs: 4 }}
          key={activity.id}
        >
          <Card
            sx={{ cursor: "pointer", padding: 2, backgroundColor: "#f5f5f5" }}
            onClick={() => navigate(`/activities/${activity.id}`)}
          >
            <CardContent>
              <Typography variant="h6">{activity.type}</Typography>
              <Typography>Duration: {activity.duration}</Typography>
              <Typography>
                Calories Burned: {activity.caloriesBurned} kcal{" "}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

export default ActivityList;
