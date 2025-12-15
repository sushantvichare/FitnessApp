import React, { useState } from "react";
import {
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";

import { addActivity } from "../services/api.js";

function ActivityForm({ onActivitiesAdded }) {
  const [activity, setActivity] = useState({
    type: "",
    duration: "",
    caloriesBurned: "",
    additionalMetrics: {},
  });

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await addActivity(activity);
      onActivitiesAdded();
      setActivity({ type: "", duration: "", caloriesBurned: "" });
    } catch (error) {
      console.error("Error adding activity:", error);
    }
  };

  return (
    <Box
      component="form"
      onSubmit={handleSubmit}
      sx={{ mb: 4, backgroundColor: "white" }}
    >
      <FormControl fullWidth sx={{ mb: 2 }}>
        {/* Form fields for activity input would go here */}
        <InputLabel>Activity Type</InputLabel>
        <Select
          value={activity.type}
          onChange={(e) => setActivity({ ...activity, type: e.target.value })}
        >
          <MenuItem value="RUNNING">Running</MenuItem>
          <MenuItem value="WALKING">Walking</MenuItem>
          <MenuItem value="CYCLING">Cycling</MenuItem>
          <MenuItem value="SWIMMING">Swimming</MenuItem>
          <MenuItem value="WEIGHT_TRAINING">Weight Training</MenuItem>
          <MenuItem value="YOGA">Yoga</MenuItem>
          <MenuItem value="HIIT">HIIT</MenuItem>
          <MenuItem value="CARDIO">Cardio</MenuItem>
          <MenuItem value="STRETCHING">Stretching</MenuItem>
          <MenuItem value="ZUMBA">Zumba</MenuItem>
          <MenuItem value="OTHER">Other</MenuItem>
        </Select>
      </FormControl>
      <TextField
        fullWidth
        sx={{ mb: 2 }}
        label="Duration (minutes)"
        type="number"
        value={activity.duration}
        onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
      ></TextField>
      <TextField
        fullWidth
        sx={{ mb: 2 }}
        label="Calories Burned"
        type="number"
        value={activity.caloriesBurned}
        onChange={(e) =>
          setActivity({ ...activity, caloriesBurned: e.target.value })
        }
      ></TextField>
      <Button type="submit" variant="contained" color="primary">
        Add Activity
      </Button>
    </Box>
  );
}

export default ActivityForm;
